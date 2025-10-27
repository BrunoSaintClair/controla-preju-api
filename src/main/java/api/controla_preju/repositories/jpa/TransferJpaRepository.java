package api.controla_preju.repositories.jpa;

import api.controla_preju.entities.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TransferJpaRepository extends JpaRepository<Transfer, UUID> {

    @Query("SELECT t FROM Transfer t WHERE t.sourceAccount.user.id = :userId OR t.destinationAccount.user.id = :userId")
    List<Transfer> findAllByUserId(@Param("userId") UUID userId);

    List<Transfer> findAllBySourceAccountId(UUID sourceAccountId);

    List<Transfer> findAllByDestinationAccountId(UUID destinationAccountId);

}
