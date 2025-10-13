package api.controla_preju.dtos.views;

import api.controla_preju.entities.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDetailsView(UUID id, String name, String email, LocalDateTime createdAt) {
    public UserDetailsView(User user){
        this(user.getId(), user.getName(), user.getEmail(), user.getCreatedAt());
    }

}
