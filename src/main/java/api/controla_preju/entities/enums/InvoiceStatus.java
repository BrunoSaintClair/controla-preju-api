package api.controla_preju.entities.enums;

public enum InvoiceStatus {
    OPEN("Aberta"),
    CLOSED("Fechada"),
    OVERDUE("Atrasada"),
    PAID("Paga");

    private final String description;

    InvoiceStatus(String description) {
        this.description = description;
    }
    public String getDescription() { return description; }
}
