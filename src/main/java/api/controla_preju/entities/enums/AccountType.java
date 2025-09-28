package api.controla_preju.entities.enums;

import lombok.Getter;

@Getter
public enum AccountType {
    CONTA_CORRENTE("Conta corrente"),
    INVESTIMENTOS("Investimentos"),
    POUPANÇA("Poupança"),
    CARTEIRA("Carteira");

    private final String description;

    AccountType(String description) {
        this.description = description;
    }

}
