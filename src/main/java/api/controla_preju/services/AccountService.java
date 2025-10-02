package api.controla_preju.services;

import api.controla_preju.dtos.forms.CreateAccountForm;
import api.controla_preju.dtos.forms.UpdateAccountForm;
import api.controla_preju.entities.Account;
import api.controla_preju.entities.User;
import api.controla_preju.exceptions.AuthorizationException;
import api.controla_preju.exceptions.BusinessException;
import api.controla_preju.repositories.AccountRepository;
import api.controla_preju.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
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

    public List<Account> findAllByUserId(UUID userId) {
        Optional<User> optional = userRepository.findById(userId);
        if (optional.isEmpty()) throw new EntityNotFoundException("Usuário não encontrado");
        return accountRepository.findAllByUser(optional.get());
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

    public void delete(Account account) {
        accountRepository.delete(account);
    }

    public Account update(Account account, UpdateAccountForm form){
        if (form.name() != null) account.setName(form.name());
        if (form.description() != null) account.setDescription(form.description());
        if (form.type() != null) account.setType(form.type());

        return accountRepository.save(account);
    }

}
