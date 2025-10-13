package api.controla_preju.repositories.jpa;

import api.controla_preju.entities.Revenue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RevenueJpaRepository extends JpaRepository<Revenue, UUID> {
    List<Revenue> findAllByAccountId(UUID accountId);
}
