package api.controla_preju.services;

import api.controla_preju.dtos.forms.CreateTransferForm;
import api.controla_preju.entities.Account;
import api.controla_preju.entities.Transfer;
import api.controla_preju.repositories.TransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
