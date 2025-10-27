package api.controla_preju.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TRANSFERS")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Transfer {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, length = 30)
    private String title;
    @Column(nullable = false, length = 50)
    private String description;
    @Column(nullable = false)
    private long amountInCents;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @ManyToOne
    private Account sourceAccount;
    @ManyToOne
    private Account destinationAccount;

    public Transfer(String title, String description, long amountInCents, LocalDateTime createdAt,
                    Account sourceAccount, Account destinationAccount) {
        this.title = title;
        this.description = description;
        this.amountInCents = amountInCents;
        this.createdAt = createdAt;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
