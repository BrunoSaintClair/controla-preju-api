package api.controla_preju.dtos.forms;

import jakarta.validation.constraints.NotNull;

public record UpdateCanChangeBalanceForm(
        @NotNull(message = "O campo 'canChangeBalance' não pode ser nulo.")
        Boolean canChangeBalance) {
}
