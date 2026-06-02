package api.controla_preju.repositories;

import api.controla_preju.entities.CreditCard;
import api.controla_preju.entities.User;
import api.controla_preju.repositories.jpa.CreditCardJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CreditCardRepository {
    private final CreditCardJpaRepository jpaRepository;

    public CreditCardRepository(CreditCardJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    public CreditCard save(CreditCard card) { return jpaRepository.save(card); }
    public Optional<CreditCard> findById(UUID id) { return jpaRepository.findById(id); }
    public List<CreditCard> findAllByUser(User user) { return jpaRepository.findAllByUser(user); }
    public boolean existsByNameAndUser(String name, User user) { return jpaRepository.existsByNameAndUser(name, user); }
    public void delete(CreditCard card) { jpaRepository.delete(card); }
}
