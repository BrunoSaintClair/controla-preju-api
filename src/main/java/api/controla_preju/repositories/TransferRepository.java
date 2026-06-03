package api.controla_preju.repositories;

import api.controla_preju.entities.Transfer;
import api.controla_preju.entities.enums.TransactionStatus;
import api.controla_preju.repositories.jpa.TransferJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    public Page<Transfer> findAllByUserId(UUID userId, Pageable pageable) {
        return transferJpaRepository.findAllByUserId(userId, pageable);
    }

    public Page<Transfer> findAllBySourceAccountId(UUID sourceAccountId, Pageable pageable) {
        return transferJpaRepository.findAllBySourceAccountId(sourceAccountId, pageable);
    }

    public Page<Transfer> findAllByDestinationAccountId(UUID destinationAccountId, Pageable pageable) {
        return transferJpaRepository.findAllByDestinationAccountId(destinationAccountId, pageable);
    }

    public Page<Transfer> findAllByUserIdAndYearAndMonth(UUID userId, int year, int month, Pageable pageable) {
        return transferJpaRepository.findAllByUserIdAndYearAndMonth(userId, year, month, pageable);
    }

    public Page<Transfer> findAllByUserIdAndYearAndMonthAndDay(UUID userId, int year, int month, int day, Pageable pageable) {
        return transferJpaRepository.findAllByUserIdAndYearAndMonthAndDay(userId, year, month, day, pageable);
    }

    public boolean existsDuplicate(String title, LocalDateTime createdAt, long amountInCents) {
        return transferJpaRepository.existsByTitleAndCreatedAtAndAmountInCents(
                title, createdAt, amountInCents
        );
    }

    public List<Transfer> findAllByStatusAndAutomaticProcessTrueAndCreatedAtBefore(TransactionStatus status, LocalDateTime dateTime) {
        return transferJpaRepository.findAllByStatusAndAutomaticProcessTrueAndCreatedAtBefore(status, dateTime);
    }

}
