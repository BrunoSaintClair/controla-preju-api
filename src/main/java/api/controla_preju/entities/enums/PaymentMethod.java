package api.controla_preju.entities.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    PIX("Pix"),
    CASH("Dinheiro"),
    CREDIT_CARD("Cartão de crédito"),
    DEBIT_CARD("Cartão de débito"),
    BANK_SLIP("Boleto");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

}
