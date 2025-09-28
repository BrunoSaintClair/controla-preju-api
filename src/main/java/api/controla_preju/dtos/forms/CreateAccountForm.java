package api.controla_preju.dtos.forms;

import api.controla_preju.entities.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAccountForm(
        @NotBlank(message = "O campo de nome da conta não pode ser vazio.")
        @Size(max = 30, message = "O nome da conta deve conter no máximo 30 caracteres.")
        String name,

        @Size(max = 50, message = "A descrição da conta deve conter no máximo 50 caracteres.")
        @NotBlank
        String description,

        @NotNull
        AccountType type
) {
}
