package api.controla_preju.repositories;

import api.controla_preju.entities.User;
import api.controla_preju.repositories.jpa.UserJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserRepository(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    public User findByEmail(String email){
        return jpaRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email){
        return jpaRepository.existsByEmail(email);
    }

    public User save(User newUser){
        return jpaRepository.save(newUser);
    }

}
