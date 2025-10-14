package api.controla_preju.entities.enums;

import lombok.Getter;

@Getter
public enum ExpenseCategory {
    OUTROS("Outros"),
    INVESTIMENTOS("Investimentos"),
    MORADIA("Moradia"),
    TRANSPORTE("Transporte"),
    ALIMENTACAO("Alimentação"),
    SUPERMERCADO("Super-mercado"),
    SAUDE("Saúde"),
    ROUPAS("Roupas"),
    APARENCIA("Aparência"),
    TECNOLOGIA("Tecnologia"),
    DIVIDAS("Dívidas");

    private final String description;

    ExpenseCategory(String description) {
        this.description = description;
    }

}
