package api.controla_preju.dtos.views;

import api.controla_preju.entities.enums.AccountType;

import java.time.LocalDateTime;
import java.util.UUID;

public record AccountDetailsView(UUID id, String name, String description,
                                 AccountType type, long balanceInCents,
                                 LocalDateTime updatedAt, LocalDateTime createdAt) {
}
