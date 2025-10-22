package api.controla_preju.repositories;

import api.controla_preju.entities.Transfer;
import api.controla_preju.repositories.jpa.TransferJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class TransferRepository {

    private final TransferJpaRepository transferJpaRepository;

    public TransferRepository(TransferJpaRepository transferJpaRepository) {
        this.transferJpaRepository = transferJpaRepository;
    }

    public Transfer save(Transfer newTransfer){
        return transferJpaRepository.save(newTransfer);
    }

}
