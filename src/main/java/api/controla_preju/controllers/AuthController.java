package api.controla_preju.controllers;

import api.controla_preju.dtos.forms.LoginForm;
import api.controla_preju.dtos.views.LoginView;
import api.controla_preju.entities.User;
import api.controla_preju.exceptions.PasswordOrEmailInvalidException;
import api.controla_preju.services.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginView> login(@Valid @RequestBody LoginForm form) {
        var emailAndPassword = new UsernamePasswordAuthenticationToken(form.email(), form.password());
        try {
            var auth = authenticationManager.authenticate(emailAndPassword);
            var token = tokenService.generateToken((User) auth.getPrincipal());
            return ResponseEntity.ok(new LoginView(token, "Bearer"));
        } catch (BadCredentialsException | InternalAuthenticationServiceException e) {
            throw new PasswordOrEmailInvalidException("Email e/ou senha inválidos.");
        }
    }

}