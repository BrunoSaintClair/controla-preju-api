package api.controla_preju.dtos.views;

import api.controla_preju.entities.Transfer;

import java.time.LocalDateTime;
import java.util.UUID;

public record TransferDetailsView(UUID id, String title, String description, Long amountInCents,
                                  LocalDateTime createdAt, UUID sourceAccountId, UUID destinationAccountId) {

    public TransferDetailsView(Transfer transfer){
        this(transfer.getId(), transfer.getTitle(), transfer.getDescription(), transfer.getAmountInCents(),
                transfer.getCreatedAt(), transfer.getSourceAccount().getId(), transfer.getDestinationAccount().getId());
    }
}
