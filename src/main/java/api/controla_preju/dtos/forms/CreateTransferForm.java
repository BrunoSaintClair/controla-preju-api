package api.controla_preju.dtos.forms;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateTransferForm(
        @NotBlank(message = "O título não pode ser vazio.")
        @Size(max = 30, message = "O título da transferência deve conter no máximo 30 caracteres.")
        String title,

        @Size(max = 50, message = "A descrição da transferência deve conter no máximo 50 caracteres.")
        String description,

        @NotNull(message = "O valor da transferência não pode ser nulo.")
        @Min(value = 1, message = "O valor da transferência deve ser no mínimo 1.")
        Long amountInCents,

        @NotNull(message = "A data de criação não pode ser nula.")
        LocalDateTime createdAt,

        @NotNull(message = "O id da conta de origem não pode ser nula.")
        UUID sourceAccountId,

        @NotNull(message = "O id da conta recebedora não pode ser nula.")
        UUID destinationAccountId
) {
}
