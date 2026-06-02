package api.controla_preju.dtos.forms;

import api.controla_preju.entities.enums.ExpenseCategory;
import api.controla_preju.entities.enums.PaymentMethod;
import api.controla_preju.entities.enums.TransactionStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateExpenseForm(
        @NotBlank @Size(max = 30) String title,
        @Size(max = 50) String description,
        @NotNull @Min(1) Long amountInCents,
        @NotNull ExpenseCategory category,
        @NotNull PaymentMethod paymentMethod,
        @NotNull LocalDateTime createdAt,
        @NotNull TransactionStatus status,
        @NotNull Boolean automaticDebit,
        UUID accountId,
        UUID creditCardId,
        @Min(value = 1, message = "O número mínimo de parcelas é 1.")
        Integer installments
) {
}
