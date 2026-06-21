package com.lari.finance.api.infrastructure.web.dto;

import com.lari.finance.api.domain.model.PaymentMethod;

public record PaymentMethodResponse(String value, String label) {
    public static PaymentMethodResponse from(PaymentMethod method) {
        return new PaymentMethodResponse(method.name(), method.label());
    }
}
