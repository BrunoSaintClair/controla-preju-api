package api.controla_preju.dtos.forms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordForm(
        @NotBlank(message = "A senha não pode ser vazia.")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
        @Size(max = 100, message = "A senha deve conter no máximo 100 caracteres.")
        String newPassword
) {
}
