package api.controla_preju.repositories;

import api.controla_preju.entities.Account;
import api.controla_preju.repositories.jpa.AccountJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepository {

    private final AccountJpaRepository jpaRepository;

    public AccountRepository(AccountJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    public Account save(Account newAccount){
        return jpaRepository.save(newAccount);
    }

}
