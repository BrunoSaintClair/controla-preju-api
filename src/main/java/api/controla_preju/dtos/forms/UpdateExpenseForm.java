package api.controla_preju.dtos.forms;

import api.controla_preju.entities.enums.ExpenseCategory;
import api.controla_preju.entities.enums.PaymentMethod;
import api.controla_preju.entities.enums.TransactionStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record UpdateExpenseForm(
        @Size(max = 30, message = "O título da despesa deve conter no máximo 30 caracteres.")
        String title,

        @Size(max = 50, message = "A descrição da despesa deve conter no máximo 50 caracteres.")
        String description,

        @Min(value = 1, message = "O valor da despesa deve ser no mínimo 1.")
        Long amountInCents,

        PaymentMethod paymentMethod,

        ExpenseCategory category,

        LocalDateTime createdAt,

        TransactionStatus status
) {
}
