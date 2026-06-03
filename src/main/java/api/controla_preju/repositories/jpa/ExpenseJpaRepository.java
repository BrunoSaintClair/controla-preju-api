package api.controla_preju.repositories.jpa;

import api.controla_preju.entities.Expense;
import api.controla_preju.entities.enums.ExpenseCategory;
import api.controla_preju.entities.enums.PaymentMethod;
import api.controla_preju.entities.enums.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ExpenseJpaRepository extends JpaRepository<Expense, UUID> {

    Page<Expense> findAllByAccountId(UUID accountId, Pageable pageable);
    Page<Expense> findAllByAccountIdAndPaymentMethod(UUID accountId, PaymentMethod paymentMethod, Pageable pageable);
    Page<Expense> findAllByAccountIdAndCategory(UUID accountId, ExpenseCategory category, Pageable pageable);

    @Query("SELECT e FROM Expense e LEFT JOIN e.account a LEFT JOIN e.invoice i LEFT JOIN i.creditCard c WHERE a.user.id = :userId OR c.user.id = :userId")
    Page<Expense> findAllByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT e FROM Expense e LEFT JOIN e.account a LEFT JOIN e.invoice i LEFT JOIN i.creditCard c WHERE (a.user.id = :userId OR c.user.id = :userId) AND e.paymentMethod = :paymentMethod")
    Page<Expense> findAllByUserIdAndPaymentMethod(@Param("userId") UUID userId, @Param("paymentMethod") PaymentMethod paymentMethod, Pageable pageable);

    @Query("SELECT e FROM Expense e LEFT JOIN e.account a LEFT JOIN e.invoice i LEFT JOIN i.creditCard c WHERE (a.user.id = :userId OR c.user.id = :userId) AND e.category = :category")
    Page<Expense> findAllByUserIdAndCategory(@Param("userId") UUID userId, @Param("category") ExpenseCategory category, Pageable pageable);

    @Query("SELECT e FROM Expense e WHERE e.account.id = :accountId AND EXTRACT(YEAR FROM e.createdAt) = :year AND EXTRACT(MONTH FROM e.createdAt) = :month")
    Page<Expense> findAllByAccountIdAndYearAndMonth(@Param("accountId") UUID accountId, @Param("year") int year, @Param("month") int month, Pageable pageable);

    @Query("SELECT e FROM Expense e WHERE e.account.id = :accountId AND EXTRACT(YEAR FROM e.createdAt) = :year AND EXTRACT(MONTH FROM e.createdAt) = :month AND EXTRACT(DAY FROM e.createdAt) = :day")
    Page<Expense> findAllByAccountIdAndYearAndMonthAndDay(@Param("accountId") UUID accountId, @Param("year") int year, @Param("month") int month, @Param("day") int day, Pageable pageable);

    boolean existsByAccountIdAndStatusAndAutomaticDebitTrue(UUID accountId, TransactionStatus status);

    boolean existsByTitleAndCreatedAtAndAmountInCentsAndCategory(String title, LocalDateTime createdAt, long amountInCents, ExpenseCategory category);

    List<Expense> findAllByStatusAndAutomaticDebitTrueAndCreatedAtBefore(TransactionStatus status, LocalDateTime dateTime);
    List<Expense> findAllByStatusAndAutomaticDebitFalseAndCreatedAtBefore(TransactionStatus status, LocalDateTime dateTime);
}
