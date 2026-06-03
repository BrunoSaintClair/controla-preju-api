package api.controla_preju.repositories.jpa;

import api.controla_preju.entities.Transfer;
import api.controla_preju.entities.enums.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransferJpaRepository extends JpaRepository<Transfer, UUID> {

    @Query("SELECT t FROM Transfer t WHERE t.sourceAccount.user.id = :userId OR t.destinationAccount.user.id = :userId")
    Page<Transfer> findAllByUserId(@Param("userId") UUID userId, Pageable pageable);

    Page<Transfer> findAllBySourceAccountId(UUID sourceAccountId, Pageable pageable);

    Page<Transfer> findAllByDestinationAccountId(UUID destinationAccountId, Pageable pageable);

    @Query("""
            SELECT t FROM Transfer t
            WHERE (t.sourceAccount.user.id = :userId OR t.destinationAccount.user.id = :userId)
            AND EXTRACT(YEAR FROM t.createdAt) = :year
            AND EXTRACT(MONTH FROM t.createdAt) = :month
            """)
    Page<Transfer> findAllByUserIdAndYearAndMonth(@Param("userId") UUID userId,
                                                  @Param("year") int year,
                                                  @Param("month") int month, Pageable pageable);

    @Query("""
            SELECT t FROM Transfer t
            WHERE (t.sourceAccount.user.id = :userId OR t.destinationAccount.user.id = :userId)
            AND EXTRACT(YEAR FROM t.createdAt) = :year
            AND EXTRACT(MONTH FROM t.createdAt) = :month
            AND EXTRACT(DAY FROM t.createdAt) = :day
            """)
    Page<Transfer> findAllByUserIdAndYearAndMonthAndDay(@Param("userId") UUID userId,
                                                        @Param("year") int year,
                                                        @Param("month") int month,
                                                        @Param("day") int day, Pageable pageable);

    boolean existsByTitleAndCreatedAtAndAmountInCents(
            String title, LocalDateTime createdAt, long amountInCents
    );

    List<Transfer> findAllByStatusAndAutomaticProcessTrueAndCreatedAtBefore(TransactionStatus status, LocalDateTime dateTime);
}
