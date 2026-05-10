package api.controla_preju.dtos.views;

import api.controla_preju.entities.Account;
import api.controla_preju.entities.enums.AccountType;

import java.util.UUID;

public record CreatedAccountView(UUID id, String name, String description,
                                 AccountType type, Long balanceInCents)
{
    public CreatedAccountView(Account account){
        this(account.getId(), account.getName(), account.getDescription(), account.getType(),
                account.getBalanceInCents());
    }
}
