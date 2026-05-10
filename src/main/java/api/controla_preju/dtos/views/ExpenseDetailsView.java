package api.controla_preju.dtos.views;

import api.controla_preju.entities.Expense;
import api.controla_preju.entities.enums.ExpenseCategory;
import api.controla_preju.entities.enums.PaymentMethod;
import api.controla_preju.entities.enums.TransactionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ExpenseDetailsView(UUID id, String title, String description,
                                 long amountInCents, PaymentMethod paymentMethod,
                                 ExpenseCategory category, LocalDateTime createdAt,
                                 TransactionStatus status, boolean automaticDebit, UUID accountId) {
    public ExpenseDetailsView(Expense expense) {
        this(expense.getId(), expense.getTitle(), expense.getDescription(),
                expense.getAmountInCents(), expense.getPaymentMethod(), expense.getCategory(),
                expense.getCreatedAt(), expense.getStatus(), expense.isAutomaticDebit(), expense.getAccount().getId());
    }
}
