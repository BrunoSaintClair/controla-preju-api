package api.controla_preju.entities;

import api.controla_preju.entities.enums.RevenueCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "REVENUES")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Revenue {
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
    private RevenueCategory category;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @ManyToOne
    private Account account;

    public Revenue(String title, String description, long amountInCents, RevenueCategory category,
                   LocalDateTime createdAt, Account account) {
        this.title = title;
        this.description = description;
        this.amountInCents = amountInCents;
        this.category = category;
        this.createdAt = createdAt;
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

    public void setCategory(RevenueCategory category) {
        this.category = category;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
