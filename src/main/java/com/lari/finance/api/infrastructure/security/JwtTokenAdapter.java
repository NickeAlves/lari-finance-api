package com.lari.finance.api.infrastructure.security;

import com.lari.finance.api.domain.model.UserAccount;
import com.lari.finance.api.domain.port.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenAdapter implements TokenService {
    private final SecretKey secretKey;
    private final long expirationMillis;

    public JwtTokenAdapter(
        @Value("${app.security.jwt.secret}") String secret,
        @Value("${app.security.jwt.expiration}") long expirationMillis
    ) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT_SECRET debe tener al menos 32 bytes.");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = expirationMillis;
    }

    @Override
    public String generate(UserAccount user) {
        Instant now = Instant.now();
        return Jwts.builder()
            .subject(user.email())
            .claim("userId", user.id().toString())
            .claim("name", user.name())
            .claim("role", user.role().name())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusMillis(expirationMillis)))
            .signWith(secretKey)
            .compact();
    }

    @Override
    public String subject(String token) {
        return claims(token).getSubject();
    }

    @Override
    public boolean isValid(String token) {
        try {
            claims(token);
            return true;
        } catch (RuntimeException exception) {
            return false;
        }
    }

    private Claims claims(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
