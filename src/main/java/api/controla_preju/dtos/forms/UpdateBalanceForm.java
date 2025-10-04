package api.controla_preju.dtos.forms;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateBalanceForm(
        @NotNull(message = "O novo saldo não pode ser nulo.")
        @Min(value = 0, message = "O saldo da conta não pode ser menor que 0.")
        Long newBalanceInCents
) {
}