package api.controla_preju.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "CREDIT_CARDS")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreditCard {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false)
    private long limitInCents;

    @Column(nullable = false)
    private long availableLimitInCents;

    @Column(nullable = false)
    private int closingDay;

    @Column(nullable = false)
    private int dueDay;

    @ManyToOne
    private User user;

    public CreditCard(String name, long limitInCents, int closingDay, int dueDay, User user) {
        this.name = name;
        this.limitInCents = limitInCents;
        this.availableLimitInCents = limitInCents;
        this.closingDay = closingDay;
        this.dueDay = dueDay;
        this.user = user;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLimitInCents(long limitInCents) {
        this.limitInCents = limitInCents;
    }

    public void setClosingDay(int closingDay) {
        this.closingDay = closingDay;
    }

    public void setAvailableLimitInCents(long availableLimitInCents) {
        this.availableLimitInCents = availableLimitInCents;
    }

    public void setDueDay(int dueDay) {
        this.dueDay = dueDay;
    }

    public void subtractLimit(long amountInCents) {
        this.availableLimitInCents -= amountInCents;
    }

    public void restoreLimit(long amountInCents) {
        this.availableLimitInCents += amountInCents;
    }

}