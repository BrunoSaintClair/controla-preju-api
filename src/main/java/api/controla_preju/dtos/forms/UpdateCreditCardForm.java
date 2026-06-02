package api.controla_preju.dtos.forms;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateCreditCardForm(
        @Size(max = 30) String name,
        @Min(1) Long limitInCents,
        @Min(1) @Max(31) Integer closingDay,
        @Min(1) @Max(31) Integer dueDay
) {
}
