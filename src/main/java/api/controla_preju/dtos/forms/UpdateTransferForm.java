package api.controla_preju.dtos.forms;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record UpdateTransferForm(
        @Size(max = 30, message = "O título da transferência deve conter no máximo 30 caracteres.")
        String title,

        @Size(max = 50, message = "A descrição da transferência deve conter no máximo 50 caracteres.")
        String description,

        @Min(value = 1, message = "O valor da transferência deve ser no mínimo 1.")
        Long amountInCents,

        LocalDateTime createdAt
) {
}