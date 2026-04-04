package api.controla_preju.dtos.forms;

import api.controla_preju.entities.enums.RevenueCategory;
import api.controla_preju.entities.enums.TransactionStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record UpdateRevenueForm(
        @Size(max = 30, message = "O título da receita deve conter no máximo 30 caracteres.")
        String title,

        @Size(max = 50, message = "A descrição da receita deve conter no máximo 50 caracteres.")
        String description,

        @Min(value = 1, message = "O valor da receita deve ser no mínimo 1.")
        Long amountInCents,

        RevenueCategory category,

        LocalDateTime createdAt,

        TransactionStatus status
) {
}
