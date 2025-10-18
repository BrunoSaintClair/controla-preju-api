package api.controla_preju.repositories.jpa;

import api.controla_preju.entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ExpenseJpaRepository extends JpaRepository<Expense, UUID> {
    List<Expense> findAllByAccountId(UUID accountId);

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

}
