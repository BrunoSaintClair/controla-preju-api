package api.controla_preju.dtos.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginForm(
        @NotBlank(message = "O e-mail não pode ser vazio.")
        @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "Formato de e-mail inválido.")
        String email,
        @NotBlank(message = "A senha não pode ser vazia.")
        String password) {
}
