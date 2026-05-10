package api.controla_preju.dtos.views;

import api.controla_preju.entities.Revenue;
import api.controla_preju.entities.enums.RevenueCategory;
import api.controla_preju.entities.enums.TransactionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record RevenueDetailsView(UUID id, String title, String description, Long amountInCents,
                                 RevenueCategory category, LocalDateTime createdAt,
                                 TransactionStatus status, boolean automaticProcess, UUID accountId) {

    public RevenueDetailsView(Revenue revenue) {
        this(revenue.getId(), revenue.getTitle(), revenue.getDescription(), revenue.getAmountInCents(),
                revenue.getCategory(), revenue.getCreatedAt(), revenue.getStatus(), revenue.isAutomaticProcess(), revenue.getAccount().getId());
    }
}
