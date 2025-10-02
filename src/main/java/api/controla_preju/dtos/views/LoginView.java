package api.controla_preju.dtos.views;

import java.util.UUID;

public record LoginView(UUID userId, String userName, String tokenType, String token) {
}
