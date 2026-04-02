package api.controla_preju.entities.enums;

import lombok.Getter;

@Getter
public enum ExpenseCategory {
    OTHERS("Outros"),
    INVESTMENTS("Investimentos"),
    HOUSING("Moradia"),
    TRANSPORT("Transporte"),
    FOOD("Alimentação"),
    GROCERIES("Super-mercado"),
    HEALTH("Saúde"),
    CLOTHING("Roupas"),
    APPEARANCE("Aparência"),
    TECHNOLOGY("Tecnologia"),
    STREAMING("Streaming"),
    SERVICES("Serviços"),
    DEBTS("Dívidas");

    private final String description;

    ExpenseCategory(String description) {
        this.description = description;
    }

}
