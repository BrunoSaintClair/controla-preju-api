package api.controla_preju.entities;

import api.controla_preju.entities.enums.InvoiceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "INVOICES")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private int month;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private long totalAmountInCents;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvoiceStatus status;

    @ManyToOne
    private CreditCard creditCard;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Expense> expenses;

    public Invoice(int month, int year, long totalAmountInCents, InvoiceStatus status, CreditCard creditCard) {
        this.month = month;
        this.year = year;
        this.totalAmountInCents = totalAmountInCents;
        this.status = status;
        this.creditCard = creditCard;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public void addAmount(long amountInCents) {
        this.totalAmountInCents += amountInCents;
    }

    public void subtractAmount(long amountInCents) {
        this.totalAmountInCents -= amountInCents;
    }

    public void setTotalAmountInCents(long totalAmountInCents) {
        this.totalAmountInCents = totalAmountInCents;
    }

}