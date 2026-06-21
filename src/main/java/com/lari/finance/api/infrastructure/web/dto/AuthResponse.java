package com.lari.finance.api.infrastructure.web.dto;

import com.lari.finance.api.application.dto.AuthResult;

import java.util.UUID;

public record AuthResponse(String token, String tokenType, UserResponse user) {
    public static AuthResponse from(AuthResult result) {
        return new AuthResponse(
            result.token(),
            "Bearer",
            new UserResponse(result.user().id(), result.user().name(), result.user().email(), result.user().role().name())
        );
    }

    public record UserResponse(UUID id, String name, String email, String role) {
    }
}
