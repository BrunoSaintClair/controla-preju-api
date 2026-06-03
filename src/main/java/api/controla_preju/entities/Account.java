package api.controla_preju.entities;

import api.controla_preju.entities.enums.AccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ACCOUNTS")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, length = 30)
    private String name;
    @Column(nullable = false, length = 50)
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AccountType type;
    @Column(nullable = false)
    private long balanceInCents;
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Revenue> revenues;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Expense> expenses;

    @OneToMany(mappedBy = "sourceAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transfer> sourceTransfers;

    @OneToMany(mappedBy = "destinationAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transfer> destinationTransfers;

    public Account(String name, String description, AccountType type, long balanceInCents, User user) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.balanceInCents = balanceInCents;
        this.createdAt = LocalDateTime.now();
        this.user = user;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public void setBalanceInCents(long balanceInCents) {
        this.balanceInCents = balanceInCents;
    }


}
