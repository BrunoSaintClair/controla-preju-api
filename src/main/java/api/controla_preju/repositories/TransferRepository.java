package api.controla_preju.repositories;

import api.controla_preju.entities.Transfer;
import api.controla_preju.repositories.jpa.TransferJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TransferRepository {

    private final TransferJpaRepository transferJpaRepository;

    public TransferRepository(TransferJpaRepository transferJpaRepository) {
        this.transferJpaRepository = transferJpaRepository;
    }

    public Transfer save(Transfer newTransfer){
        return transferJpaRepository.save(newTransfer);
    }

    public Optional<Transfer> findById(UUID id) {
        return transferJpaRepository.findById(id);
    }

    public void delete(Transfer transfer) {
        transferJpaRepository.delete(transfer);
    }

    public List<Transfer> findAllByUserId(UUID userId) {
        return transferJpaRepository.findAllByUserId(userId);
    }

    public List<Transfer> findAllBySourceAccountId(UUID sourceAccountId) {
        return transferJpaRepository.findAllBySourceAccountId(sourceAccountId);
    }

    public List<Transfer> findAllByDestinationAccountId(UUID destinationAccountId) {
        return transferJpaRepository.findAllByDestinationAccountId(destinationAccountId);
    }

    public List<Transfer> findAllByUserIdAndYearAndMonth(UUID userId, int year, int month) {
        return transferJpaRepository.findAllByUserIdAndYearAndMonth(userId, year, month);
    }

    public List<Transfer> findAllByUserIdAndYearAndMonthAndDay(UUID userId, int year, int month, int day) {
        return transferJpaRepository.findAllByUserIdAndYearAndMonthAndDay(userId, year, month, day);
    }

}
