package api.controla_preju.services;

import api.controla_preju.dtos.forms.CreateAccountForm;
import api.controla_preju.entities.Account;
import api.controla_preju.entities.User;
import api.controla_preju.exceptions.AuthorizationException;
import api.controla_preju.exceptions.BusinessException;
import api.controla_preju.repositories.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Account create(CreateAccountForm form, User owner){
        if (accountRepository.existsByNameAndUser(form.name(), owner)) {
            throw new BusinessException("O usuário já possui uma conta com este nome.");
        }
        Account newAccount = new Account(
                form.name(),
                form.description(),
                form.type(),
                owner
        );
        return accountRepository.save(newAccount);
    }

    public Account findById(UUID accountId, UUID userId) {
        Optional<Account> optional = accountRepository.findById(accountId);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("Conta não encontrada");
        }

        Account entity = optional.get();

        if (!entity.getUser().getId().equals(userId)){
            throw new AuthorizationException("Essa conta não pertence ao usuário que está efetuando a requisição.");
        }

        return entity;
    }

}
