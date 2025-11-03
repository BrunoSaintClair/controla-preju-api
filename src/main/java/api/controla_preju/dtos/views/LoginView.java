package api.controla_preju.dtos.views;

import api.controla_preju.entities.enums.Role;

import java.util.UUID;

public record LoginView(UUID userId, String userName, Role role, String tokenType, String token) {
}
