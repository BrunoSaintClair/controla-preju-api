package api.controla_preju.services;

import api.controla_preju.dtos.forms.CreateTransferForm;
import api.controla_preju.entities.Account;
import api.controla_preju.entities.Transfer;
import api.controla_preju.exceptions.AuthorizationException;
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
        userService.findById(userId);

        Account sourceAccount = accountService.findById(form.sourceAccountId(), userId);
        Account destinationAccount = accountService.findById(form.destinationAccountId(), userId);

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

    public List<Transfer> findAllByUserId(UUID userId) {
        return transferRepository.findAllByUserId(userId);
    }

}
