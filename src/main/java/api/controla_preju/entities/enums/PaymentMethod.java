package api.controla_preju.entities.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    PIX("Pix"),
    DINHEIRO("Dinheiro"),
    CREDITO("Cartão de crédito"),
    DEBITO("Cartão de débito"),
    BOLETO("Boleto");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

}
