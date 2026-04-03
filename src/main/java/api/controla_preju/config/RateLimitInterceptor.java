package api.controla_preju.config;

import api.controla_preju.exceptions.RateLimitExceededException;
import api.controla_preju.services.RateLimitingService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitingService rateLimitingService;

    public RateLimitInterceptor(RateLimitingService rateLimitingService) {
        this.rateLimitingService = rateLimitingService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }

        Bucket bucket = rateLimitingService.resolveBucket(clientIp);

        if (bucket.tryConsume(1)) {
            return true;
        } else {
            throw new RateLimitExceededException("Limite de requisições excedido. Tente novamente em um minuto.");
        }
    }
}