package api.controla_preju.repositories.jpa;

import api.controla_preju.dtos.views.TransactionHistoryView;
import api.controla_preju.entities.Account;
import api.controla_preju.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountJpaRepository extends JpaRepository<Account, UUID>  {
    boolean existsByNameAndUser(String name, User user);
    List<Account> findAllByUser(User user);
    int countByUser(User user);

    @Query("SELECT SUM(a.balanceInCents) FROM Account a WHERE a.user.id = :userId")
    Optional<Long> sumBalanceByUserId(@Param("userId") UUID userId);

    @Query(value = """
        SELECT
            id, title, amount_in_cents, created_at, type, account_id, status
        FROM (
            SELECT r.id, r.title, r.amount_in_cents, r.created_at, 'REVENUE' AS type, r.account_id, r.status
            FROM Revenues r
            JOIN Accounts a ON r.account_id = a.id
            WHERE a.user_id = :userId
        UNION ALL
            SELECT e.id, e.title, e.amount_in_cents, e.created_at, 'EXPENSE' AS type, e.account_id, e.status
            FROM Expenses e
            JOIN Accounts a ON e.account_id = a.id
            WHERE a.user_id = :userId
        UNION ALL
            SELECT t.id, t.title, t.amount_in_cents, t.created_at, 'TRANSFER_SENT' AS type, t.source_account_id, 'COMPLETED' AS status
            FROM Transfers t
            JOIN Accounts a ON t.source_account_id = a.id
            WHERE a.user_id = :userId
        UNION ALL
            SELECT t.id, t.title, t.amount_in_cents, t.created_at, 'TRANSFER_RECEIVED' AS type, t.destination_account_id, 'COMPLETED' AS status
            FROM Transfers t
            JOIN Accounts a ON t.destination_account_id = a.id
            WHERE a.user_id = :userId
        ) AS transactions
        ORDER BY created_at DESC
    """, countQuery = """
        SELECT COUNT(*) FROM (
            SELECT r.id FROM Revenues r JOIN Accounts a ON r.account_id = a.id WHERE a.user_id = :userId
            UNION
            SELECT e.id FROM Expenses e JOIN Accounts a ON e.account_id = a.id WHERE a.user_id = :userId
            UNION
            SELECT t.id FROM Transfers t JOIN Accounts a ON t.source_account_id = a.id WHERE a.user_id = :userId
            UNION
            SELECT t.id FROM Transfers t JOIN Accounts a ON t.destination_account_id = a.id WHERE a.user_id = :userId
        ) AS total_transactions
    """, nativeQuery = true)
    Page<TransactionHistoryView> getTransactionHistoryByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Modifying
    @Query("UPDATE Account a SET a.balanceInCents = a.balanceInCents - :amount WHERE a.id = :accountId AND a.balanceInCents >= :amount")
    int subtractBalanceIfSufficient(@Param("accountId") UUID accountId, @Param("amount") long amount);

}
