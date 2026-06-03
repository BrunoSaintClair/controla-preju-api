package api.controla_preju.dtos.forms;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestForm(@NotBlank String refreshToken) {
}
