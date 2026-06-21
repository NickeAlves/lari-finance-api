package com.lari.finance.api.infrastructure.web.controller;

import com.lari.finance.api.application.service.AuthService;
import com.lari.finance.api.infrastructure.web.dto.AuthRequests.LoginRequest;
import com.lari.finance.api.infrastructure.web.dto.AuthRequests.RegisterRequest;
import com.lari.finance.api.infrastructure.web.dto.AuthResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return AuthResponse.from(authService.register(request.name(), request.email(), request.password()));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return AuthResponse.from(authService.login(request.email(), request.password()));
    }
}
