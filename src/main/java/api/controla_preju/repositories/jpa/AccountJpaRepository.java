package api.controla_preju.repositories.jpa;

import api.controla_preju.entities.Account;
import api.controla_preju.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountJpaRepository extends JpaRepository<Account, UUID>  {
    boolean existsByNameAndUser(String name, User user);
    List<Account> findAllByUser(User user);
}
