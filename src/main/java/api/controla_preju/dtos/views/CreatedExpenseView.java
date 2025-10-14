package api.controla_preju.dtos.views;

import api.controla_preju.entities.Expense;
import api.controla_preju.entities.enums.ExpenseCategory;
import api.controla_preju.entities.enums.PaymentMethod;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreatedExpenseView(UUID id, String title, String description, long amountInCents,
                                 ExpenseCategory category, PaymentMethod paymentMethod,
                                 LocalDateTime createdAt, UUID accountId)
{
    public CreatedExpenseView(Expense expense){
        this(expense.getId(), expense.getTitle(), expense.getDescription(), expense.getAmountInCents(),
                expense.getCategory(), expense.getPaymentMethod(), expense.getCreatedAt(), expense.getAccount().getId());
    }

}
