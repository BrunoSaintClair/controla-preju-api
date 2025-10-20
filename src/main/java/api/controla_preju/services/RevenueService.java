package api.controla_preju.services;

import api.controla_preju.dtos.forms.CreateRevenueForm;
import api.controla_preju.dtos.forms.UpdateRevenueForm;
import api.controla_preju.entities.Account;
import api.controla_preju.entities.Revenue;
import api.controla_preju.entities.User;
import api.controla_preju.entities.enums.RevenueCategory;
import api.controla_preju.exceptions.AuthorizationException;
import api.controla_preju.exceptions.BusinessException;
import api.controla_preju.repositories.RevenueRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class RevenueService {

    private final RevenueRepository revenueRepository;
    private final AccountService accountService;

    public RevenueService(RevenueRepository revenueRepository, AccountService accountService) {
        this.revenueRepository = revenueRepository;
        this.accountService = accountService;
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
        Account account = accountService.findById(form.accountId(), owner.getId());

        Revenue revenue = new Revenue(
                form.title(),
                form.description(),
                form.amountInCents(),
                form.category(),
                form.createdAt(),
                account
        );

        account.setBalanceInCents(account.getBalanceInCents() + revenue.getAmountInCents());
        return revenueRepository.save(revenue);
    }

    @Transactional
    public void delete(Revenue revenue) {
        Account account = revenue.getAccount();
        account.setBalanceInCents(account.getBalanceInCents() - revenue.getAmountInCents());
        revenueRepository.delete(revenue);
    }

    @Transactional
    public Revenue update(Revenue revenue, UpdateRevenueForm form) {
        Account account = revenue.getAccount();
        long oldAmount = revenue.getAmountInCents();

        if (form.title() != null) revenue.setTitle(form.title());
        if (form.description() != null) revenue.setDescription(form.description());
        if (form.category() != null) revenue.setCategory(form.category());
        if (form.createdAt() != null) revenue.setCreatedAt(form.createdAt());
        if (form.amountInCents() != null) revenue.setAmountInCents(form.amountInCents());

        long newAmount = revenue.getAmountInCents();
        long difference = newAmount - oldAmount;
        account.setBalanceInCents(account.getBalanceInCents() + difference);

        return revenueRepository.save(revenue);
    }

    public List<Revenue> findAllByUserId(UUID userId, Optional<RevenueCategory> category) {
        if (category.isPresent()) {
            return revenueRepository.findAllByUserIdAndCategory(userId, category.get());
        }
        return revenueRepository.findAllByUserId(userId);
    }

    public List<Revenue> findRevenuesByAccount(UUID accountId, UUID userId,
                                               Optional<Integer> year, Optional<Integer> month,
                                               Optional<Integer> day, Optional<RevenueCategory> category) {
        accountService.findById(accountId, userId);

        if (category.isPresent()) {
            return revenueRepository.findAllByAccountIdAndCategory(accountId, category.get());
        }
        if (year.isPresent() && month.isPresent() && day.isPresent()) {
            return revenueRepository.findAllByAccountIdAndYearAndMonthAndDay(accountId, year.get(), month.get(), day.get());
        }
        if (year.isPresent() && month.isPresent()) {
            return revenueRepository.findAllByAccountIdAndYearAndMonth(accountId, year.get(), month.get());
        }
        if (year.isEmpty() && month.isEmpty() && day.isEmpty()) {
            return revenueRepository.findAllByAccountId(accountId);
        }

        throw new BusinessException("Combinação de filtros inválida ou não suportada.");
    }


}
