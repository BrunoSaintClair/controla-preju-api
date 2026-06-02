package api.controla_preju.dtos.views;

import api.controla_preju.entities.CreditCard;
import java.util.UUID;

public record CreditCardDetailsView(
        UUID id, String name, long limitInCents, long availableLimitInCents, int closingDay, int dueDay
) {
    public CreditCardDetailsView(CreditCard card) {
        this(card.getId(), card.getName(), card.getLimitInCents(), card.getAvailableLimitInCents(), card.getClosingDay(), card.getDueDay());
    }
}
