package api.controla_preju.dtos.forms;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record PayInvoiceForm(
        @NotNull UUID accountId
) {
}
