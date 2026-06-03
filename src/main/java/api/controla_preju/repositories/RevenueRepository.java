package api.controla_preju.repositories;

import api.controla_preju.entities.Revenue;
import api.controla_preju.entities.enums.RevenueCategory;
import api.controla_preju.entities.enums.TransactionStatus;
import api.controla_preju.repositories.jpa.RevenueJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RevenueRepository {

    private final RevenueJpaRepository revenueJpaRepository;

    public RevenueRepository(RevenueJpaRepository revenueJpaRepository) {
        this.revenueJpaRepository = revenueJpaRepository;
    }

    public Optional<Revenue> findById(UUID id) {
        return revenueJpaRepository.findById(id);
    }

    public Revenue save(Revenue revenue){
        return revenueJpaRepository.save(revenue);
    }

    public void delete(Revenue revenue) {
        revenueJpaRepository.delete(revenue);
    }

    public Page<Revenue> findAllByUserId(UUID userId, Pageable pageable){
        return revenueJpaRepository.findAllByUserId(userId, pageable);
    }

    public Page<Revenue> findAllByAccountId(UUID accountId, Pageable pageable) {
        return revenueJpaRepository.findAllByAccountId(accountId, pageable);
    }

    public Page<Revenue> findAllByAccountIdAndYearAndMonth(UUID accountId, int year, int month, Pageable pageable) {
        return revenueJpaRepository.findAllByAccountIdAndYearAndMonth(accountId, year, month, pageable);
    }

    public Page<Revenue> findAllByAccountIdAndYearAndMonthAndDay(UUID accountId, int year, int month, int day, Pageable pageable) {
        return revenueJpaRepository.findAllByAccountIdAndYearAndMonthAndDay(accountId, year, month, day, pageable);
    }

    public Page<Revenue> findAllByUserIdAndCategory(UUID userId, RevenueCategory category, Pageable pageable) {
        return revenueJpaRepository.findAllByUserIdAndCategory(userId, category, pageable);
    }

    public Page<Revenue> findAllByAccountIdAndCategory(UUID accountId, RevenueCategory category, Pageable pageable) {
        return revenueJpaRepository.findAllByAccountIdAndCategory(accountId, category, pageable);
    }

    public boolean existsDuplicate(String title, LocalDateTime createdAt, long amountInCents, RevenueCategory category) {
        return revenueJpaRepository.existsByTitleAndCreatedAtAndAmountInCentsAndCategory(
                title, createdAt, amountInCents, category
        );
    }

    public List<Revenue> findAllByStatusAndAutomaticProcessTrueAndCreatedAtBefore(TransactionStatus status, LocalDateTime dateTime) {
        return revenueJpaRepository.findAllByStatusAndAutomaticProcessTrueAndCreatedAtBefore(status, dateTime);
    }

}
