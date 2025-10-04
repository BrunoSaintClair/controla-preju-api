package api.controla_preju.dtos.views;

import api.controla_preju.entities.Account;
import api.controla_preju.entities.enums.AccountType;

import java.time.LocalDateTime;
import java.util.UUID;

public record AccountDetailsView(UUID id, String name, String description,
                                 AccountType type, long balanceInCents,
                                 Boolean canChangeBalance, LocalDateTime updatedAt,
                                 LocalDateTime createdAt)
{
    public AccountDetailsView(Account account) {
        this(account.getId(), account.getName(), account.getDescription(), account.getType(),
                account.getBalanceInCents(), account.isCanChangeBalance(), account.getUpdatedAt(), account.getCreatedAt());
    }
}
