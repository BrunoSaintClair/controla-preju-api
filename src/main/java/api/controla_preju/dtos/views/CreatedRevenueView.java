package api.controla_preju.dtos.views;

import api.controla_preju.entities.Revenue;
import api.controla_preju.entities.enums.RevenueCategory;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreatedRevenueView(UUID id, String title, String description, long amountInCents,
                                 RevenueCategory category, LocalDateTime createdAt, UUID accountId)
{
    public CreatedRevenueView(Revenue revenue){
        this(revenue.getId(), revenue.getTitle(), revenue.getDescription(), revenue.getAmountInCents(),
                revenue.getCategory(), revenue.getCreatedAt(), revenue.getAccount().getId());
    }
}
