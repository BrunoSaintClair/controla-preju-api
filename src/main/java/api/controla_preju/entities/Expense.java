package api.controla_preju.entities;

import api.controla_preju.entities.enums.ExpenseCategory;
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
    private ExpenseCategory category;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @ManyToOne
    private Account account;

    public Expense(String title, String description, long amountInCents, ExpenseCategory category,
                   LocalDateTime createdAt, Account account) {
        this.title = title;
        this.description = description;
        this.amountInCents = amountInCents;
        this.category = category;
        this.createdAt = createdAt;
        this.account = account;
    }

}
