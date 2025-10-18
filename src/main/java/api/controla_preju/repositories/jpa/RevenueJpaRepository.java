package api.controla_preju.repositories.jpa;

import api.controla_preju.entities.Revenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RevenueJpaRepository extends JpaRepository<Revenue, UUID> {
    List<Revenue> findAllByAccountId(UUID accountId);

    @Query("SELECT r FROM Revenue r WHERE r.account.user.id = :userId")
    List<Revenue> findAllByUserId(@Param("userId") UUID userId);

    @Query("""
            SELECT r FROM Revenue r
            WHERE r.account.id = :accountId
            AND EXTRACT(YEAR FROM r.createdAt) = :year
            AND EXTRACT(MONTH FROM r.createdAt) = :month
            """)
    List<Revenue> findAllByAccountIdAndYearAndMonth(UUID accountId, int year, int month);

    @Query("""
            SELECT r FROM Revenue r
            WHERE r.account.id = :accountId
            AND EXTRACT(YEAR FROM r.createdAt) = :year
            AND EXTRACT(MONTH FROM r.createdAt) = :month
            AND EXTRACT(DAY FROM r.createdAt) = :day
            """)
    List<Revenue> findAllByAccountIdAndYearAndMonthAndDay(UUID accountId, int year, int month, int day);

}
