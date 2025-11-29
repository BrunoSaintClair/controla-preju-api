package api.controla_preju.services;

import api.controla_preju.dtos.forms.CreateUserForm;
import api.controla_preju.entities.User;
import api.controla_preju.exceptions.BusinessException;
import api.controla_preju.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TokenService tokenService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       EmailService emailService, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.tokenService = tokenService;
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

        User savedUser = userRepository.save(newUser);
        emailService.sendRegisterEmail(savedUser);
        return savedUser;
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

    @Transactional
    public void updatePassword(User user, String newPassword) {
        String encryptedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encryptedPassword);
        userRepository.save(user);
    }

    public void requestResetPassword(String email) {
        Optional<User> optional = userRepository.findByEmailNoValidation(email);
        optional.ifPresent(emailService::sendResetPasswordEmail);
    }

    @Transactional
    public void completePasswordReset(String token, String newPassword) {
        String email = tokenService.validateToken(token);

        if (email == null) {
            throw new BusinessException("Token de recuperação inválido ou expirado.");
        }

        User user = userRepository.findByEmailNoValidation(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        String encryptedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encryptedPassword);
        if (user.getStatus() != 'A') user.setStatus('A');
        userRepository.save(user);
    }

}
