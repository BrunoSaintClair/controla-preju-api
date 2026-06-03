package api.controla_preju.services;

import api.controla_preju.dtos.forms.CreateRevenueForm;
import api.controla_preju.dtos.forms.UpdateRevenueForm;
import api.controla_preju.entities.Account;
import api.controla_preju.entities.Revenue;
import api.controla_preju.entities.User;
import api.controla_preju.entities.enums.RevenueCategory;
import api.controla_preju.entities.enums.TransactionStatus;
import api.controla_preju.exceptions.AuthorizationException;
import api.controla_preju.exceptions.BusinessException;
import api.controla_preju.repositories.AccountRepository;
import api.controla_preju.repositories.RevenueRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RevenueService {

    private final RevenueRepository revenueRepository;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final TransactionTemplate transactionTemplate;

    public RevenueService(RevenueRepository revenueRepository, AccountService accountService, AccountRepository accountRepository, PlatformTransactionManager transactionManager) {
        this.revenueRepository = revenueRepository;
        this.accountService = accountService;
        this.accountRepository = accountRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    public Revenue findById(UUID revenueId, UUID userId) {
        Optional<Revenue> optionalRevenue = revenueRepository.findById(revenueId);
        if (optionalRevenue.isEmpty()) {
            throw new EntityNotFoundException("Receita não encontrada.");
        }

        Revenue revenue = optionalRevenue.get();
        if (!revenue.getAccount().getUser().getId().equals(userId)) {
            throw new AuthorizationException("Esta receita não pertence ao usuário que está efetuando a requisição.");
        }

        return revenue;
    }

    @Transactional
    public Revenue create(CreateRevenueForm form, User owner){
        if (revenueRepository.existsDuplicate(form.title(), form.createdAt(), form.amountInCents(), form.category())) {
            throw new BusinessException("Uma receita com exatamente os mesmos dados já foi registrada.");
        }

        if (form.status() == TransactionStatus.COMPLETED && form.createdAt().isAfter(LocalDateTime.now().plusMinutes(2))) {
            throw new BusinessException("Transações com data futura devem ser registradas como PENDENTES.");
        }

        if (form.automaticProcess()) {
            if (form.createdAt().isBefore(LocalDateTime.now())) {
                throw new BusinessException("Receitas automáticas devem ter data de criação futura.");
            }
            if (form.status() == TransactionStatus.COMPLETED) {
                throw new BusinessException("Receitas automáticas não podem ser criadas já como COMPLETED.");
            }
        }

        Account account = accountService.findById(form.accountId(), owner.getId());

        if (form.status() == TransactionStatus.COMPLETED) {
            account.setBalanceInCents(account.getBalanceInCents() + form.amountInCents());
        }

        Revenue revenue = new Revenue(
                form.title(),
                form.description(),
                form.amountInCents(),
                form.category(),
                form.createdAt(),
                form.status(),
                form.automaticProcess(),
                account
        );

        return revenueRepository.save(revenue);
    }

    @Transactional
    public void delete(Revenue revenue) {
        if (revenue.getStatus() == TransactionStatus.COMPLETED) {
            Account account = revenue.getAccount();
            account.setBalanceInCents(account.getBalanceInCents() - revenue.getAmountInCents());
        }
        revenueRepository.delete(revenue);
    }

    @Transactional
    public Revenue update(Revenue revenue, UpdateRevenueForm form) {
        Account account = revenue.getAccount();

        TransactionStatus oldStatus = revenue.getStatus();
        long oldAmount = revenue.getAmountInCents();

        if (form.title() != null) {
            if (form.title().isBlank()) {
                throw new BusinessException("O título da receita não pode ser vazio.");
            }
            revenue.setTitle(form.title());
        }
        if (form.description() != null) {
            if (form.description().isBlank()) {
                throw new BusinessException("A descrição da receita não pode ser vazia.");
            }
            revenue.setDescription(form.description());
        }
        if (form.category() != null) revenue.setCategory(form.category());
        if (form.createdAt() != null) revenue.setCreatedAt(form.createdAt());
        if (form.amountInCents() != null) revenue.setAmountInCents(form.amountInCents());
        if (form.status() != null) revenue.setStatus(form.status());
        if (form.automaticProcess() != null) revenue.setAutomaticProcess(form.automaticProcess());

        if (revenue.getStatus() == TransactionStatus.COMPLETED && revenue.getCreatedAt().isAfter(LocalDateTime.now().plusMinutes(2))) {
            throw new BusinessException("Transações com data futura devem ser registradas como PENDENTES.");
        }

        if (oldStatus == TransactionStatus.COMPLETED
                && form.automaticProcess() != null
                && form.automaticProcess() != revenue.isAutomaticProcess()) {
            throw new BusinessException("Não é possível alterar o processo automático de uma receita já concluída.");
        }

        if (revenue.getStatus() == TransactionStatus.COMPLETED && revenue.isAutomaticProcess()) {
            revenue.setAutomaticProcess(false);
        }

        if (revenue.isAutomaticProcess() && revenue.getStatus() == TransactionStatus.PENDING) {
            if (revenue.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(2))) {
                throw new BusinessException("A data de uma receita automática não pode estar no passado.");
            }
        }

        long effectiveOldImpact = (oldStatus == TransactionStatus.COMPLETED) ? oldAmount : 0;
        long effectiveNewImpact = (revenue.getStatus() == TransactionStatus.COMPLETED) ? revenue.getAmountInCents() : 0;
        long differenceToAdd = effectiveNewImpact - effectiveOldImpact;

        account.setBalanceInCents(account.getBalanceInCents() + differenceToAdd);

        return revenueRepository.save(revenue);
    }

    public Page<Revenue> findAllByUserId(UUID userId, Optional<RevenueCategory> category, Pageable pageable) {
        if (category.isPresent()) {
            return revenueRepository.findAllByUserIdAndCategory(userId, category.get(), pageable);
        }
        return revenueRepository.findAllByUserId(userId, pageable);
    }

    public Page<Revenue> findRevenuesByAccount(UUID accountId, UUID userId,
                                               Optional<Integer> year, Optional<Integer> month,
                                               Optional<Integer> day, Optional<RevenueCategory> category,
                                               Pageable pageable) {
        accountService.findById(accountId, userId);

        if (category.isPresent()) {
            return revenueRepository.findAllByAccountIdAndCategory(accountId, category.get(), pageable);
        }
        if (year.isPresent() && month.isPresent() && day.isPresent()) {
            return revenueRepository.findAllByAccountIdAndYearAndMonthAndDay(accountId, year.get(), month.get(), day.get(), pageable);
        }
        if (year.isPresent() && month.isPresent()) {
            return revenueRepository.findAllByAccountIdAndYearAndMonth(accountId, year.get(), month.get(), pageable);
        }
        if (year.isEmpty() && month.isEmpty() && day.isEmpty()) {
            return revenueRepository.findAllByAccountId(accountId, pageable);
        }

        throw new BusinessException("Combinação de filtros inválida ou não suportada.");
    }

    public void processAutomaticRevenues() {
        LocalDateTime now = LocalDateTime.now();
        List<Revenue> pendingRevenues = revenueRepository.findAllByStatusAndAutomaticProcessTrueAndCreatedAtBefore(TransactionStatus.PENDING, now);

        for (Revenue revenue : pendingRevenues) {
            transactionTemplate.executeWithoutResult(status -> {
                try {
                    accountRepository.addBalance(revenue.getAccount().getId(), revenue.getAmountInCents());
                    revenue.setStatus(TransactionStatus.COMPLETED);
                    revenue.setAutomaticProcess(false);
                    revenueRepository.save(revenue);
                } catch (Exception e) {
                    status.setRollbackOnly();
                }
            });
        }
    }

}
