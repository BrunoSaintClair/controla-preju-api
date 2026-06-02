package api.controla_preju.repositories.jpa;

import api.controla_preju.entities.CreditCard;
import api.controla_preju.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface CreditCardJpaRepository extends JpaRepository<CreditCard, UUID> {
    List<CreditCard> findAllByUser(User user);
    boolean existsByNameAndUser(String name, User user);
}
