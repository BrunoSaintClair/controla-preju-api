package api.controla_preju.services;

import api.controla_preju.dtos.views.TokenView;
import api.controla_preju.entities.RefreshToken;
import api.controla_preju.entities.User;
import api.controla_preju.exceptions.BusinessException;
import api.controla_preju.exceptions.PasswordOrEmailInvalidException;
import api.controla_preju.repositories.jpa.RefreshTokenRepository;
import api.controla_preju.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${api.security.refresh-token.expiration-hours}")
    private int REFRESH_TOKEN_EXPIRATION_HOURS;

    public AuthService(UserRepository userRepository, TokenService tokenService, RefreshTokenRepository refreshTokenRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username);
    }

    @Transactional
    public TokenView login(String email, String password) {
        UserDetails userDetails = loadUserByUsername(email);

        if (userDetails == null || !passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new PasswordOrEmailInvalidException("Email e/ou senha inválidos.");
        }

        User user = (User) userDetails;
        String accessToken = tokenService.generateToken(user);
        String refreshToken = createRefreshToken(user).getToken();

        return new TokenView(accessToken, refreshToken);
    }

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        refreshTokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        Instant expiryDate = LocalDateTime.now().plusHours(REFRESH_TOKEN_EXPIRATION_HOURS).toInstant(ZoneOffset.of("-03:00"));
        RefreshToken refreshToken = new RefreshToken(user, token, expiryDate);
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public TokenView refreshAccessToken(String refreshTokenString) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenString)
                .orElseThrow(() -> new BusinessException("Refresh token inválido ou não encontrado."));

        if (refreshToken.isExpired() || refreshToken.isRevoked()) {
            refreshTokenRepository.delete(refreshToken);
            throw new BusinessException("Refresh token expirado ou revogado. Faça login novamente.");
        }

        String newAccessToken = tokenService.generateToken(refreshToken.getUser());

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        RefreshToken newRefreshToken = createRefreshToken(refreshToken.getUser());

        return new TokenView(newAccessToken, newRefreshToken.getToken());
    }

    @Transactional
    public void revokeRefreshToken(String refreshTokenString) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenString)
                .orElseThrow(() -> new BusinessException("Refresh token inválido ou não encontrado."));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }
}
