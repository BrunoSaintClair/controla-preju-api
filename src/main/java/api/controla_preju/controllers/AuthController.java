package api.controla_preju.controllers;

import api.controla_preju.dtos.forms.LoginForm;
import api.controla_preju.dtos.views.LoginView;
import api.controla_preju.entities.User;
import api.controla_preju.exceptions.PasswordOrEmailInvalidException;
import api.controla_preju.repositories.UserRepository;
import api.controla_preju.services.TokenService;
import api.controla_preju.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService,
                          UserRepository userRepository, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginView> login(@Valid @RequestBody LoginForm form) {
        var emailAndPassword = new UsernamePasswordAuthenticationToken(form.email(), form.password());
        try {
            var auth = authenticationManager.authenticate(emailAndPassword);
            var user = (User) auth.getPrincipal();
            var token = tokenService.generateToken((User) auth.getPrincipal());
            return ResponseEntity.ok(new LoginView(user.getId(), user.getName(), user.getRole(), "Bearer", token));
        } catch (BadCredentialsException | InternalAuthenticationServiceException e) {
            throw new PasswordOrEmailInvalidException("Email e/ou senha inválidos.");
        }
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirmRegistration(@RequestParam("token") String token) {
        String email = tokenService.validateToken(token);
        if (email == null) {
            return ResponseEntity.badRequest().body("Token inválido ou expirado.");
        }

        User user = userRepository.findByEmailNoValidation(email)
                .orElseThrow(() -> new EntityNotFoundException("Email não encontrado."));

        if (user.getStatus() == 'P') {
            userService.reactivate(user);
            return ResponseEntity.ok("E-mail confirmado com sucesso! Pode fazer login.");
        }

        return ResponseEntity.ok("E-mail já estava confirmado.");
    }

    @GetMapping("/reject")
    public ResponseEntity<String> rejectRegistration(@RequestParam("token") String token) {
        String email = tokenService.validateToken(token);
        if (email == null) {
            return ResponseEntity.badRequest().body("Token inválido ou expirado.");
        }

        User user = userRepository.findByEmailNoValidation(email)
                .orElseThrow(() -> new EntityNotFoundException("Email não encontrado."));

        if (user.getStatus() == 'P') {
            userRepository.delete(user);
            return ResponseEntity.ok("Cadastro rejeitado com sucesso.");
        }

        return ResponseEntity.ok("O cadastro não pôde ser rejeitado.");
    }

    @GetMapping("/reset-password/confirm")
    public ResponseEntity<String> confirmReset(@RequestParam("token") String token) {
        String email = tokenService.validateToken(token);
        return ResponseEntity.ok("Aceito");
    }

    @GetMapping("/reset-password/reject")
    public ResponseEntity<String> rejectReset(@RequestParam("token") String token) {
        String email = tokenService.validateToken(token);
        return ResponseEntity.ok("Rejeitado");
    }

}
