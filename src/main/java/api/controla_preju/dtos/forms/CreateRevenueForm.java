package api.controla_preju.dtos.forms;

import api.controla_preju.entities.enums.RevenueCategory;
import api.controla_preju.entities.enums.TransactionStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateRevenueForm(
        @NotBlank(message = "O título não pode ser vazio.")
        @Size(max = 30, message = "O título da receita deve conter no máximo 30 caracteres.")
        String title,

        @Size(max = 50, message = "A descrição da receita deve conter no máximo 50 caracteres.")
        String description,

        @NotNull(message = "O valor da receita não pode ser nulo.")
        @Min(value = 1, message = "O valor da receita deve ser no mínimo 1.")
        Long amountInCents,

        @NotNull(message = "A categoria não pode ser nula.")
        RevenueCategory category,

        @NotNull(message = "A data de criação não pode ser nula.")
        LocalDateTime createdAt,

        @NotNull(message = "O status da transação não pode ser nulo.")
        TransactionStatus status,

        @NotNull(message = "O campo de automação não pode ser nulo.")
        Boolean automaticProcess,

        @NotNull(message = "O id da conta não pode ser nulo.")
        UUID accountId
) {
}
