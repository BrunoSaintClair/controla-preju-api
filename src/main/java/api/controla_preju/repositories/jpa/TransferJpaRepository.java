package api.controla_preju.repositories.jpa;

import api.controla_preju.entities.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransferJpaRepository extends JpaRepository<Transfer, UUID> {
}
