package api.controla_preju.entities.enums;

import lombok.Getter;

@Getter
public enum RevenueCategory {
    OTHERS("Outros"),
    SALARY("Salário"),
    INVESTMENTS("Investimentos");

    private final String description;

    RevenueCategory(String description) {
        this.description = description;
    }

}
