package api.controla_preju.dtos.views;

import api.controla_preju.entities.Invoice;
import api.controla_preju.entities.enums.InvoiceStatus;
import java.util.UUID;

public record InvoiceDetailsView(
        UUID id, int month, int year, long totalAmountInCents, InvoiceStatus status, UUID creditCardId
) {
    public InvoiceDetailsView(Invoice invoice) {
        this(invoice.getId(), invoice.getMonth(), invoice.getYear(), invoice.getTotalAmountInCents(), invoice.getStatus(), invoice.getCreditCard().getId());
    }
}
