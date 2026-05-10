package api.controla_preju.services;

import api.controla_preju.dtos.forms.CreateAccountForm;
import api.controla_preju.dtos.forms.UpdateAccountForm;
import api.controla_preju.dtos.views.TransactionHistoryView;
import api.controla_preju.entities.Account;
import api.controla_preju.entities.User;
import api.controla_preju.exceptions.AuthorizationException;
import api.controla_preju.exceptions.BusinessException;
import api.controla_preju.repositories.AccountRepository;
import api.controla_preju.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    @Value("${api.max-accounts-per-user}")
    private int maxAccountsPerUser;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Account create(CreateAccountForm form, User owner){
        if (accountRepository.existsByNameAndUser(form.name(), owner)) {
            throw new BusinessException("O usuário já possui uma conta com este nome.");
        }
        if (accountRepository.countByUser(owner) >= maxAccountsPerUser){
            throw new BusinessException("O usuário atingiu o limite de quantidade de contas.");
        }

        Account newAccount = new Account(
                form.name(),
                form.description(),
                form.type(),
                form.initialBalance(),
                owner
        );
        return accountRepository.save(newAccount);
    }

    public List<Account> findAllByUserId(UUID userId) {
        User user = userRepository.findById(userId);
        return accountRepository.findAllByUser(user);
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

    @Transactional
    public void delete(Account account) {
        accountRepository.delete(account);
    }

    @Transactional
    public Account update(Account account, UpdateAccountForm form){
        if (form.name() != null) {
            if (form.name().isBlank()) {
                throw new BusinessException("O nome da conta não pode ser vazio.");
            }
            account.setName(form.name());
        }
        if (form.description() != null) {
            if (form.description().isBlank()) {
                throw new BusinessException("A descrição da conta não pode ser vazia.");
            }
            account.setDescription(form.description());
        }
        if (form.type() != null) account.setType(form.type());

        return accountRepository.save(account);
    }

    public long getTotalBalanceByUserId(UUID userId) {
        return accountRepository.sumBalanceByUserId(userId).orElse(0L);
    }

    public Page<TransactionHistoryView> getTransactionHistory(UUID userId, Pageable pageable) {
        return accountRepository.getTransactionHistoryByUserId(userId, pageable);
    }

}
