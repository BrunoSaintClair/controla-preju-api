package api.controla_preju.repositories.jpa;

import api.controla_preju.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountJpaRepository extends JpaRepository<Account, UUID>  {
}
