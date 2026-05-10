package api.controla_preju.dtos.views;

import java.sql.Timestamp;
import java.util.UUID;

public record TransactionHistoryView(
        UUID id,
        String title,
        Long amountInCents,
        Timestamp createdAt,
        String type,
        UUID accountId,
        String status
) {
}
