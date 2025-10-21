package api.controla_preju.dtos.forms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserNameForm(
        @NotBlank(message = "O nome não pode ser vazio.")
        @Size(min = 4, message = "O nome deve conter pelo menos 4 caracteres.")
        @Size(max = 30, message = "O nome deve conter no máximo 30 caracteres.")
        String name
) {
}