package com.lari.finance.api.application.service;

import com.lari.finance.api.application.dto.AuthResult;
import com.lari.finance.api.application.exception.BusinessException;
import com.lari.finance.api.domain.model.UserAccount;
import com.lari.finance.api.domain.port.TokenService;
import com.lari.finance.api.domain.port.UserAccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final boolean registrationEnabled;

    public AuthService(
        UserAccountRepository userAccountRepository,
        PasswordEncoder passwordEncoder,
        TokenService tokenService,
        @Value("${app.security.registration-enabled}") boolean registrationEnabled
    ) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.registrationEnabled = registrationEnabled;
    }

    @Transactional
    public AuthResult register(String name, String email, String password) {
        if (!registrationEnabled) {
            throw new BusinessException("El registro esta desactivado.");
        }
        String normalizedEmail = normalizeEmail(email);
        if (userAccountRepository.existsByEmail(normalizedEmail)) {
            throw new BusinessException("Ya existe una cuenta con este correo.");
        }
        UserAccount user = UserAccount.createOwner(name, normalizedEmail, passwordEncoder.encode(password));
        UserAccount saved = userAccountRepository.save(user);
        return new AuthResult(tokenService.generate(saved), saved);
    }

    @Transactional(readOnly = true)
    public AuthResult login(String email, String password) {
        UserAccount user = userAccountRepository.findByEmail(normalizeEmail(email))
            .orElseThrow(() -> new BadCredentialsException("Credenciales invalidas."));
        if (!passwordEncoder.matches(password, user.passwordHash())) {
            throw new BadCredentialsException("Credenciales invalidas.");
        }
        return new AuthResult(tokenService.generate(user), user);
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BadCredentialsException("Credenciales invalidas.");
        }
        return email.trim().toLowerCase();
    }
}
