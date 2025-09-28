package api.controla_preju.dtos.views;

import api.controla_preju.entities.User;
import api.controla_preju.entities.enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreatedUserView(
        UUID id,
        String email,
        String name,
        Role role,
        LocalDateTime createdAt
) {
    public CreatedUserView(User user) {
        this(user.getId(), user.getEmail(), user.getName(), user.getRole(), user.getCreatedAt());
    }
}
