package api.controla_preju.repositories.jpa;

import api.controla_preju.entities.Expense;
import api.controla_preju.entities.enums.ExpenseCategory;
import api.controla_preju.entities.enums.PaymentMethod;
import api.controla_preju.entities.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ExpenseJpaRepository extends JpaRepository<Expense, UUID> {
    List<Expense> findAllByAccountId(UUID accountId);

    List<Expense> findAllByAccountIdAndPaymentMethod(UUID accountId, PaymentMethod paymentMethod);

    List<Expense> findAllByAccountIdAndCategory(UUID accountId, ExpenseCategory category);

    @Query("SELECT e FROM Expense e WHERE e.account.user.id = :userId AND e.paymentMethod = :paymentMethod")
    List<Expense> findAllByUserIdAndPaymentMethod(@Param("userId") UUID userId,
                                                  @Param("paymentMethod") PaymentMethod paymentMethod);

    @Query("SELECT e FROM Expense e WHERE e.account.user.id = :userId")
    List<Expense> findAllByUserId(@Param("userId") UUID userId);

    @Query("""
            SELECT e FROM Expense e
            WHERE e.account.id = :accountId
            AND EXTRACT(YEAR FROM e.createdAt) = :year
            AND EXTRACT(MONTH FROM e.createdAt) = :month
            """)
    List<Expense> findAllByAccountIdAndYearAndMonth(UUID accountId, int year, int month);

    @Query("""
            SELECT e FROM Expense e
            WHERE e.account.id = :accountId
            AND EXTRACT(YEAR FROM e.createdAt) = :year
            AND EXTRACT(MONTH FROM e.createdAt) = :month
            AND EXTRACT(DAY FROM e.createdAt) = :day
            """)
    List<Expense> findAllByAccountIdAndYearAndMonthAndDay(UUID accountId, int year, int month, int day);

    @Query("SELECT e FROM Expense e WHERE e.account.user.id = :userId AND e.category = :category")
    List<Expense> findAllByUserIdAndCategory(@Param("userId") UUID userId, @Param("category") ExpenseCategory category);

    boolean existsByTitleAndCreatedAtAndAmountInCentsAndCategory(
            String title, LocalDateTime createdAt, long amountInCents, ExpenseCategory category
    );

    List<Expense> findAllByStatusAndAutomaticDebitTrueAndCreatedAtBefore(
            TransactionStatus status, LocalDateTime dateTime
    );

    List<Expense> findAllByStatusAndAutomaticDebitFalseAndCreatedAtBefore(
            TransactionStatus status, LocalDateTime dateTime
    );

}
