package api.controla_preju.repositories;

import api.controla_preju.entities.Revenue;
import api.controla_preju.repositories.jpa.RevenueJpaRepository;
import org.springframework.stereotype.Repository;

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

    public List<Revenue> findAllByAccountId(UUID accountId) {
        return revenueJpaRepository.findAllByAccountId(accountId);
    }

    public List<Revenue> findAllByAccountIdAndYearAndMonth(UUID accountId, int year, int month) {
        return revenueJpaRepository.findAllByAccountIdAndYearAndMonth(accountId, year, month);
    }

    public List<Revenue> findAllByAccountIdAndYearAndMonthAndDay(UUID accountId, int year, int month, int day) {
        return revenueJpaRepository.findAllByAccountIdAndYearAndMonthAndDay(accountId, year, month, day);
    }

}
