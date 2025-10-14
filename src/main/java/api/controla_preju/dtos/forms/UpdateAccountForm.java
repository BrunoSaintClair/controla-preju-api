package api.controla_preju.dtos.forms;

import api.controla_preju.entities.enums.AccountType;
import jakarta.validation.constraints.Size;

public record UpdateAccountForm(
        @Size(max = 30, message = "O nome da conta deve conter no máximo 30 caracteres.")
        String name,

        @Size(max = 50, message = "A descrição da conta deve conter no máximo 50 caracteres.")
        String description,

        AccountType type
) {
}
