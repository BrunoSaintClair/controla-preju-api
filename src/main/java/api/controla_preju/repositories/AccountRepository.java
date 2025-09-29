package api.controla_preju.repositories;

import api.controla_preju.entities.Account;
import api.controla_preju.entities.User;
import api.controla_preju.repositories.jpa.AccountJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AccountRepository {

    private final AccountJpaRepository jpaRepository;

    public AccountRepository(AccountJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    public Account save(Account newAccount){
        return jpaRepository.save(newAccount);
    }

    public boolean existsByNameAndUser(String name, User user) {
        return jpaRepository.existsByNameAndUser(name, user);
    }

    public List<Account> findAllByUser(User user) {
        return jpaRepository.findAllByUser(user);
    }

    public Optional<Account> findById(UUID accountId){
        return jpaRepository.findById(accountId);
    }

}
