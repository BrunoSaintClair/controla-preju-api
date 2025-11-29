package api.controla_preju.repositories;

import api.controla_preju.entities.User;
import api.controla_preju.exceptions.BusinessException;
import api.controla_preju.repositories.jpa.UserJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserRepository(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    private User validateUser(Optional<User> optionalUser) {
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("Usuário não encontrado.");
        }
        User user = optionalUser.get();
        if (!user.isEnabled()) {
            throw new BusinessException("Usuário com a conta desativada.");
        }
        return user;
    }

    public User findByEmail(String email){
        return validateUser(jpaRepository.findByEmail(email));
    }

    public Optional<User> findByEmailNoValidation(String email) {
        return jpaRepository.findByEmail(email);
    }

    public User findById(UUID id){
        return validateUser(jpaRepository.findById(id));
    }

    public boolean existsByEmail(String email){
        return jpaRepository.existsByEmail(email);
    }

    public User save(User newUser){
        return jpaRepository.save(newUser);
    }

    public void delete(User user) {
        jpaRepository.delete(user);
    }

}
