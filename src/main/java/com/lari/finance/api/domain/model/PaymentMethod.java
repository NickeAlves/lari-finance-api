package com.lari.finance.api.domain.model;

public enum PaymentMethod {
    EFECTIVO("Efectivo"),
    TARJETA("Tarjeta"),
    BIZUM("Bizum"),
    TRANSFERENCIA("Transferencia"),
    OTRO("Otro");

    private final String label;

    PaymentMethod(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
