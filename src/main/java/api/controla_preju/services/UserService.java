package api.controla_preju.services;

import api.controla_preju.dtos.forms.CreateUserForm;
import api.controla_preju.entities.User;
import api.controla_preju.exceptions.BusinessException;
import api.controla_preju.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User findById(UUID id){
        return userRepository.findById(id);
    }

    @Transactional
    public User create(CreateUserForm form){
        if (userRepository.existsByEmail(form.email())) {
            throw new BusinessException("Este e-mail já está em uso.");
        }

        String encryptedPassword = passwordEncoder.encode(form.password());

        User newUser = new User(
                form.email(),
                form.name(),
                encryptedPassword
        );

        return userRepository.save(newUser);
    }

    @Transactional
    public void deactivate(User user) {
        user.setStatus('I');
        userRepository.save(user);
    }

    @Transactional
    public void reactivate(User user) {
        user.setStatus('A');
        userRepository.save(user);
    }

    @Transactional
    public User updateName(User user, String newName) {
        user.setName(newName);
        return userRepository.save(user);
    }

}
