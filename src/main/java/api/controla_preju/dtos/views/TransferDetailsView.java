package api.controla_preju.dtos.views;

import api.controla_preju.entities.Transfer;
import api.controla_preju.entities.enums.TransactionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record TransferDetailsView(UUID id, String title, String description, Long amountInCents,
                                  LocalDateTime createdAt, TransactionStatus status, boolean automaticProcess,
                                  UUID sourceAccountId, UUID destinationAccountId) {

    public TransferDetailsView(Transfer transfer){
        this(transfer.getId(), transfer.getTitle(), transfer.getDescription(), transfer.getAmountInCents(),
                transfer.getCreatedAt(), transfer.getStatus(), transfer.isAutomaticProcess(), transfer.getSourceAccount().getId(), transfer.getDestinationAccount().getId());
    }
}
