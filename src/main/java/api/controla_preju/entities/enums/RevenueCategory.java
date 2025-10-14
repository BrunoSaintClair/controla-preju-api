package api.controla_preju.entities.enums;

import lombok.Getter;

@Getter
public enum RevenueCategory {
    OUTROS("Outros"),
    SALARIO("Salário"),
    INVESTIMENTOS("Investimentos");

    private final String description;

    RevenueCategory(String description) {
        this.description = description;
    }

}
