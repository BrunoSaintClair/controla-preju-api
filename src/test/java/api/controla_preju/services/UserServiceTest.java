package api.controla_preju.services;

import api.controla_preju.dtos.forms.CreateUserForm;
import api.controla_preju.entities.User;
import api.controla_preju.entities.enums.UserStatus;
import api.controla_preju.exceptions.BusinessException;
import api.controla_preju.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserService userService;

    private CreateUserForm form;
    private User user;

    @BeforeEach
    void setUp() {
        form = new CreateUserForm("test@email.com", "Test user", "123456");
        user = new User("test@email.com", "test name", "password");
    }


    @Test
    @DisplayName("Should create user succesfully")
    void shouldCreateUser() {
        when(userRepository.existsByEmail(form.email())).thenReturn(false);
        when(passwordEncoder.encode(form.password())).thenReturn("encryptedpassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User createdUser = userService.create(form);

        verify(userRepository).existsByEmail(form.email());
        verify(passwordEncoder).encode(form.password());
        verify(userRepository).save(any(User.class));

        assertNotNull(createdUser);
        assertEquals("test@email.com", createdUser.getEmail());
        assertEquals("encryptedpassword", createdUser.getPassword());
    }

    @Test
    @DisplayName("Should throw exception of not unique email while creating user")
    void shouldThrowExceptionCreatingUser() {
        when(userRepository.existsByEmail(form.email())).thenReturn(true);

        assertThrows(BusinessException.class, () -> userService.create(form));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should deactivate user succesfully")
    void shouldDeactivate() {
        userService.deactivate(user);
        verify(userRepository).save(user);
        assertEquals(UserStatus.INACTIVE, user.getStatus());
    }

    @Test
    @DisplayName("Should reactivate user succesfully")
    void shouldReactivate() {
        assertEquals(UserStatus.PENDING, user.getStatus());
        user.setStatus(UserStatus.INACTIVE);
        userService.reactivate(user);
        verify(userRepository).save(user);
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }

}
