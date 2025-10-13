package api.controla_preju.repositories;

import api.controla_preju.entities.Revenue;
import api.controla_preju.repositories.jpa.RevenueJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class RevenueRepository {

    private final RevenueJpaRepository revenueJpaRepository;

    public RevenueRepository(RevenueJpaRepository revenueJpaRepository) {
        this.revenueJpaRepository = revenueJpaRepository;
    }

    public Revenue save(Revenue revenue){
        return revenueJpaRepository.save(revenue);
    }

}
