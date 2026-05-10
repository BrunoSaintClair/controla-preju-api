package api.controla_preju.services;

import api.controla_preju.dtos.forms.CreateTransferForm;
import api.controla_preju.dtos.forms.UpdateTransferForm;
import api.controla_preju.entities.Account;
import api.controla_preju.entities.Transfer;
import api.controla_preju.entities.User;
import api.controla_preju.entities.enums.AccountType;
import api.controla_preju.entities.enums.TransactionStatus;
import api.controla_preju.exceptions.AuthorizationException;
import api.controla_preju.exceptions.BusinessException;
import api.controla_preju.repositories.AccountRepository;
import api.controla_preju.repositories.TransferRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final UserService userService;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final TransactionTemplate transactionTemplate;

    public TransferService(TransferRepository transferRepository, UserService userService,
                           AccountService accountService, AccountRepository accountRepository,
                           EmailService emailService, PlatformTransactionManager transactionManager) {
        this.transferRepository = transferRepository;
        this.userService = userService;
        this.accountService = accountService;
        this.accountRepository = accountRepository;
        this.emailService = emailService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Transactional
    public Transfer create(CreateTransferForm form, UUID userId){
        if (transferRepository.existsDuplicate(form.title(), form.createdAt(), form.amountInCents())) {
            throw new BusinessException("Uma transferência com exatamente os mesmos dados já foi registrada.");
        }

        if (form.status() == TransactionStatus.COMPLETED && form.createdAt().isAfter(LocalDateTime.now().plusMinutes(2))) {
            throw new BusinessException("Transações com data futura devem ser registradas como PENDENTES.");
        }

        if (form.automaticProcess()) {
            if (form.createdAt().isBefore(LocalDateTime.now())) {
                throw new BusinessException("Transferências automáticas devem ter data de criação futura.");
            }
            if (form.status() == TransactionStatus.COMPLETED) {
                throw new BusinessException("Transferências automáticas não podem ser criadas já como COMPLETED.");
            }
        }

        userService.findById(userId);

        Account sourceAccount = accountService.findById(form.sourceAccountId(), userId);
        Account destinationAccount = accountService.findById(form.destinationAccountId(), userId);

        if (form.sourceAccountId().equals(form.destinationAccountId())) {
            throw new BusinessException("A conta de origem e destino não podem ser a mesma.");
        }

        if (form.automaticProcess() && sourceAccount.getType() == AccountType.WALLET) {
            throw new BusinessException("Contas do tipo Carteira não suportam transferências automáticas.");
        }

        if (form.status() == TransactionStatus.COMPLETED) {
            if (sourceAccount.getBalanceInCents() < form.amountInCents()) {
                throw new BusinessException("Conta de origem não possui saldo suficiente para a transferência.");
            }
            sourceAccount.setBalanceInCents(sourceAccount.getBalanceInCents() - form.amountInCents());
            destinationAccount.setBalanceInCents(destinationAccount.getBalanceInCents() + form.amountInCents());
        }

        Transfer newTransfer = new Transfer(
                form.title(),
                form.description(),
                form.amountInCents(),
                form.createdAt(),
                form.status(),
                form.automaticProcess(),
                sourceAccount,
                destinationAccount
        );

        return transferRepository.save(newTransfer);
    }

    public Transfer findById(UUID transferId, UUID userId) {
        Optional<Transfer> optionalTransfer = transferRepository.findById(transferId);
        if (optionalTransfer.isEmpty()) {
            throw new EntityNotFoundException("Transferência não encontrada.");
        }

        Transfer transfer = optionalTransfer.get();
        boolean ownsSource = transfer.getSourceAccount().getUser().getId().equals(userId);
        boolean ownsDestination = transfer.getDestinationAccount().getUser().getId().equals(userId);

        if (!ownsSource || !ownsDestination) {
            throw new AuthorizationException("Esta transferência não pertence ao usuário que está efetuando a requisição.");
        }
        return transfer;
    }

    @Transactional
    public void delete(Transfer transfer) {
        if (transfer.getStatus() == TransactionStatus.COMPLETED) {
            Account sourceAccount = transfer.getSourceAccount();
            Account destinationAccount = transfer.getDestinationAccount();
            long amount = transfer.getAmountInCents();

            sourceAccount.setBalanceInCents(sourceAccount.getBalanceInCents() + amount);
            destinationAccount.setBalanceInCents(destinationAccount.getBalanceInCents() - amount);
        }
        transferRepository.delete(transfer);
    }

    public List<Transfer> findAllByUserId(UUID userId, Optional<Integer> year, Optional<Integer> month, Optional<Integer> day) {
        if (year.isPresent() && month.isPresent() && day.isPresent()) {
            return transferRepository.findAllByUserIdAndYearAndMonthAndDay(userId, year.get(), month.get(), day.get());
        }
        if (year.isPresent() && month.isPresent()) {
            return transferRepository.findAllByUserIdAndYearAndMonth(userId, year.get(), month.get());
        }
        if (year.isEmpty() && month.isEmpty() && day.isEmpty()) {
            return transferRepository.findAllByUserId(userId);
        }

        throw new BusinessException("Combinação de filtros de data inválida. Forneça ano/mês ou ano/mês/dia.");
    }

    public List<Transfer> findAllBySourceAccount(UUID accountId, UUID userId) {
        accountService.findById(accountId, userId);
        return transferRepository.findAllBySourceAccountId(accountId);
    }

    public List<Transfer> findAllByDestinationAccount(UUID accountId, UUID userId) {
        accountService.findById(accountId, userId);
        return transferRepository.findAllByDestinationAccountId(accountId);
    }

    @Transactional
    public Transfer update(Transfer transfer, UpdateTransferForm form) {
        Account sourceAccount = transfer.getSourceAccount();
        Account destinationAccount = transfer.getDestinationAccount();

        TransactionStatus oldStatus = transfer.getStatus();
        long oldAmount = transfer.getAmountInCents();

        if (form.title() != null) {
            if (form.title().isBlank()) {
                throw new BusinessException("O título da transferência não pode ser vazio.");
            }
            transfer.setTitle(form.title());
        }
        if (form.description() != null) {
            if (form.description().isBlank()) {
                throw new BusinessException("A descrição da transferência não pode ser vazia.");
            }
            transfer.setDescription(form.description());
        }
        if (form.createdAt() != null) transfer.setCreatedAt(form.createdAt());
        if (form.amountInCents() != null) transfer.setAmountInCents(form.amountInCents());
        if (form.status() != null) transfer.setStatus(form.status());
        if (form.automaticProcess() != null) transfer.setAutomaticProcess(form.automaticProcess());

        if (transfer.getStatus() == TransactionStatus.COMPLETED && transfer.getCreatedAt().isAfter(LocalDateTime.now().plusMinutes(2))) {
            throw new BusinessException("Transações com data futura devem ser registradas como PENDENTES.");
        }

        if (oldStatus == TransactionStatus.COMPLETED
                && form.automaticProcess() != null
                && form.automaticProcess() != transfer.isAutomaticProcess()) {
            throw new BusinessException("Não é possível alterar o processo automático de uma transferência já concluída.");
        }

        if (transfer.getStatus() == TransactionStatus.COMPLETED && transfer.isAutomaticProcess()) {
            transfer.setAutomaticProcess(false);
        }

        if (transfer.isAutomaticProcess() && transfer.getStatus() == TransactionStatus.PENDING) {
            if (sourceAccount.getType() == AccountType.WALLET) {
                throw new BusinessException("Contas do tipo Carteira não suportam transferências automáticas.");
            }
            if (transfer.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(2))) {
                throw new BusinessException("A data de uma transferência automática não pode estar no passado.");
            }
        }

        long oldImpact = (oldStatus == TransactionStatus.COMPLETED) ? oldAmount : 0;
        long newImpact = (transfer.getStatus() == TransactionStatus.COMPLETED) ? transfer.getAmountInCents() : 0;
        long difference = newImpact - oldImpact;

        if (difference != 0) {
            long newSourceBalance = sourceAccount.getBalanceInCents() - difference;
            if (newSourceBalance < 0) {
                throw new BusinessException("Saldo insuficiente na conta de origem para atualizar a transferência.");
            }
            sourceAccount.setBalanceInCents(newSourceBalance);
            destinationAccount.setBalanceInCents(destinationAccount.getBalanceInCents() + difference);
        }

        return transferRepository.save(transfer);
    }

    public void processAutomaticTransfers() {
        LocalDateTime now = LocalDateTime.now();
        List<Transfer> pendingTransfers = transferRepository.findAllByStatusAndAutomaticProcessTrueAndCreatedAtBefore(TransactionStatus.PENDING, now);

        Map<User, List<Transfer>> failedTransfersByUser = new HashMap<>();

        for (Transfer transfer : pendingTransfers) {
            transactionTemplate.executeWithoutResult(status -> {
                try {
                    int updatedRows = accountRepository.subtractBalanceIfSufficient(
                            transfer.getSourceAccount().getId(), transfer.getAmountInCents());

                    if (updatedRows > 0) {
                        accountRepository.addBalance(transfer.getDestinationAccount().getId(), transfer.getAmountInCents());
                        transfer.setStatus(TransactionStatus.COMPLETED);
                        transfer.setAutomaticProcess(false);
                    } else {
                        transfer.setStatus(TransactionStatus.FAILED);
                        transfer.setAutomaticProcess(false);
                        failedTransfersByUser.computeIfAbsent(transfer.getSourceAccount().getUser(), k -> new ArrayList<>()).add(transfer);
                    }
                    transferRepository.save(transfer);
                } catch (Exception e) {
                    status.setRollbackOnly();
                }
            });
        }

        for (Map.Entry<User, List<Transfer>> entry : failedTransfersByUser.entrySet()) {
            try {
                emailService.sendFailedAutomaticTransfersEmail(entry.getKey(), entry.getValue());
            } catch (Exception ignored) {
            }
        }
    }

}
