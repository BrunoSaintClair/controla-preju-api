package api.controla_preju.entities;

import api.controla_preju.entities.enums.ExpenseCategory;
import api.controla_preju.entities.enums.PaymentMethod;
import api.controla_preju.entities.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "EXPENSES")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Expense {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, length = 30)
    private String title;
    @Column(nullable = false, length = 50)
    private String description;
    @Column(nullable = false)
    private long amountInCents;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentMethod paymentMethod;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ExpenseCategory category;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TransactionStatus status;
    @Column(nullable = false)
    private boolean automaticDebit;
    @ManyToOne
    private Account account;

    public Expense(String title, String description, long amountInCents, PaymentMethod paymentMethod,
                   ExpenseCategory category, LocalDateTime createdAt, TransactionStatus status,
                   boolean automaticDebit, Account account) {
        this.title = title;
        this.description = description;
        this.amountInCents = amountInCents;
        this.paymentMethod = paymentMethod;
        this.category = category;
        this.createdAt = createdAt;
        this.status = status;
        this.automaticDebit = automaticDebit;
        this.account = account;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmountInCents(long amountInCents) {
        this.amountInCents = amountInCents;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public boolean isAutomaticDebit() { return automaticDebit; }

    public void setAutomaticDebit(boolean automaticDebit) { this.automaticDebit = automaticDebit; }

}
