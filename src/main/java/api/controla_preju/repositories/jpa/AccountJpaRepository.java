package api.controla_preju.repositories.jpa;

import api.controla_preju.entities.Account;
import api.controla_preju.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountJpaRepository extends JpaRepository<Account, UUID>  {
    boolean existsByNameAndUser(String name, User user);
    List<Account> findAllByUser(User user);
    int countByUser(User user);

    @Query("SELECT SUM(a.balanceInCents) FROM Account a WHERE a.user.id = :userId")
    Optional<Long> sumBalanceByUserId(@Param("userId") UUID userId);

}
