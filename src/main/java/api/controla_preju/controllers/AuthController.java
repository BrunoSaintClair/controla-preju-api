package api.controla_preju.controllers;

import api.controla_preju.dtos.forms.LoginForm;
import api.controla_preju.dtos.forms.RefreshTokenRequestForm;
import api.controla_preju.dtos.views.LoginView;
import api.controla_preju.dtos.views.TokenView;
import api.controla_preju.entities.User;
import api.controla_preju.entities.enums.UserStatus;
import api.controla_preju.exceptions.PasswordOrEmailInvalidException;
import api.controla_preju.repositories.UserRepository;
import api.controla_preju.services.AuthService;
import api.controla_preju.services.TokenService;
import api.controla_preju.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final AuthService authService;

    public AuthController(TokenService tokenService,
                          UserRepository userRepository, UserService userService, AuthService authService) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginView> login(@Valid @RequestBody LoginForm form) {
        try {
            TokenView tokenView = authService.login(form.email(), form.password());
            User user = (User) authService.loadUserByUsername(form.email());
            return ResponseEntity.ok(new LoginView(user.getId(), user.getName(), user.getRole(), "Bearer", tokenView.accessToken(), tokenView.refreshToken()));
        } catch (BadCredentialsException | InternalAuthenticationServiceException e) {
            throw new PasswordOrEmailInvalidException("Email e/ou senha inválidos.");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginView> refresh(@Valid @RequestBody RefreshTokenRequestForm form) {
        TokenView tokenView = authService.refreshAccessToken(form.refreshToken());
        return ResponseEntity.ok(new LoginView(null, null, null, "Bearer", tokenView.accessToken(), tokenView.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequestForm form) {
        authService.revokeRefreshToken(form.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/confirm-registration")
    public ResponseEntity<String> confirmRegistration(@RequestParam("token") String token) {
        String email = tokenService.validateToken(token);
        if (email == null) {
            return ResponseEntity.badRequest().body("Token inválido ou expirado.");
        }

        User user = userRepository.findByEmailNoValidation(email).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("Token inválido, expirado ou link já utilizado.");
        }

        if (user.getStatus() == UserStatus.PENDING) {
            userService.reactivate(user);
            return ResponseEntity.ok("E-mail confirmado com sucesso! Pode fazer login.");
        }

        return ResponseEntity.ok("E-mail já estava confirmado.");
    }

    @GetMapping("/reject-registration")
    public ResponseEntity<String> rejectRegistration(@RequestParam("token") String token) {
        String email = tokenService.validateToken(token);
        if (email == null) {
            return ResponseEntity.badRequest().body("Token inválido ou expirado.");
        }

        User user = userRepository.findByEmailNoValidation(email).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("Token inválido, expirado ou link já utilizado.");
        }

        if (user.getStatus() == UserStatus.PENDING) {
            userRepository.delete(user);
            return ResponseEntity.ok("Cadastro rejeitado com sucesso.");
        }

        return ResponseEntity.ok("O cadastro não pôde ser rejeitado.");
    }

}
