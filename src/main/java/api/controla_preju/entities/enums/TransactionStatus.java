package api.controla_preju.entities.enums;

import lombok.Getter;

@Getter
public enum TransactionStatus {
    PENDING("Pendente"),
    COMPLETED("Concluída");

    private final String description;

    TransactionStatus(String description) {
        this.description = description;
    }

}
