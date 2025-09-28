package api.controla_preju.services;

import api.controla_preju.dtos.forms.CreateAccountForm;
import api.controla_preju.entities.Account;
import api.controla_preju.entities.User;
import api.controla_preju.repositories.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Account create(CreateAccountForm form, User owner){
        Account newAccount = new Account(
                form.name(),
                form.description(),
                form.type(),
                owner
        );
        return accountRepository.save(newAccount);
    }

}
