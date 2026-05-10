package api.controla_preju.dtos.forms;

import api.controla_preju.entities.enums.AccountType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAccountForm(
        @Size(max = 30, message = "O nome da conta deve conter no máximo 30 caracteres.")
        @NotBlank(message = "O campo de nome da conta não pode ser vazio.")
        String name,

        @Size(max = 50, message = "A descrição da conta deve conter no máximo 50 caracteres.")
        @NotBlank(message = "O campo de descrição da conta não pode ser vazio.")
        String description,

        @NotNull(message = "O campo de tipo da conta não pode ser vazio.")
        AccountType type,

        @NotNull(message = "O campo de saldo inicial da conta não pode ser vazio.")
        @Min(value = 0, message = "O saldo inicial não pode ser menor que 0.")
        Long initialBalance

) {
}
