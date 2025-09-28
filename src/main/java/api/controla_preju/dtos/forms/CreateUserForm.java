package api.controla_preju.dtos.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserForm(
        @NotBlank(message = "O e-mail não pode ser vazio.")
        @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "Formato de e-mail inválido.")
        @Size(max = 150, message = "O e-mail deve conter no máximo 150 caracteres.")
        String email,

        @NotBlank(message = "O nome não pode ser vazio.")
        @Size(min = 4, message = "O nome deve conter pelo menos 4 caracteres.")
        @Size(max = 100, message = "O nome deve conter no máximo 100 caracteres.")
        String name,

        @NotBlank(message = "A senha não pode ser vazia.")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
        @Size(max = 100, message = "A senha deve conter no máximo 100 caracteres.")
        String password
) {
}