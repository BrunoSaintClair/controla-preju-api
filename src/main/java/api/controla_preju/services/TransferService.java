package api.controla_preju.services;

import api.controla_preju.dtos.forms.CreateTransferForm;
import api.controla_preju.dtos.forms.UpdateTransferForm;
import api.controla_preju.entities.Account;
import api.controla_preju.entities.Transfer;
import api.controla_preju.exceptions.AuthorizationException;
import api.controla_preju.exceptions.BusinessException;
import api.controla_preju.repositories.TransferRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final UserService userService;
    private final AccountService accountService;

    public TransferService(TransferRepository transferRepository, UserService userService,
                           AccountService accountService) {
        this.transferRepository = transferRepository;
        this.userService = userService;
        this.accountService = accountService;
    }


    @Transactional
    public Transfer create(CreateTransferForm form, UUID userId){
        if (transferRepository.existsDuplicate(form.title(), form.createdAt(), form.amountInCents())) {
            throw new BusinessException("Uma transferência com exatamente os mesmos dados já foi registrada.");
        }

        userService.findById(userId);

        Account sourceAccount = accountService.findById(form.sourceAccountId(), userId);
        Account destinationAccount = accountService.findById(form.destinationAccountId(), userId);

        if (form.sourceAccountId().equals(form.destinationAccountId())) {
            throw new BusinessException("A conta de origem e destino não podem ser a mesma.");
        }

        Transfer newTransfer = new Transfer(
                form.title(),
                form.description(),
                form.amountInCents(),
                form.createdAt(),
                sourceAccount,
                destinationAccount
        );

        sourceAccount.setBalanceInCents(sourceAccount.getBalanceInCents() - form.amountInCents());
        destinationAccount.setBalanceInCents(destinationAccount.getBalanceInCents() + form.amountInCents());

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
        Account sourceAccount = transfer.getSourceAccount();
        Account destinationAccount = transfer.getDestinationAccount();
        long amount = transfer.getAmountInCents();

        sourceAccount.setBalanceInCents(sourceAccount.getBalanceInCents() + amount);
        destinationAccount.setBalanceInCents(destinationAccount.getBalanceInCents() - amount);

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

        long newAmount = transfer.getAmountInCents();
        long difference = newAmount - oldAmount;
        sourceAccount.setBalanceInCents(sourceAccount.getBalanceInCents() - difference);
        destinationAccount.setBalanceInCents(destinationAccount.getBalanceInCents() + difference);

        return transferRepository.save(transfer);
    }

}
