package api.controla_preju.entities.enums;

import lombok.Getter;

@Getter
public enum AccountType {
    CHECKING_ACCOUNT("Conta corrente"),
    INVESTMENTS("Investimentos"),
    SAVINGS_ACCOUNT("Poupança"),
    WALLET("Carteira");

    private final String description;

    AccountType(String description) {
        this.description = description;
    }

}
