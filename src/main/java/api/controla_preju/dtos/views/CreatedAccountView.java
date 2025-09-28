package api.controla_preju.dtos.views;

import api.controla_preju.entities.enums.AccountType;

import java.util.UUID;

public record CreatedAccountView(UUID id, String name, String description, AccountType type) {
}
