package com.lari.finance.api.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class UserAccount {
    private final UUID id;
    private final String name;
    private final String email;
    private final String passwordHash;
    private final UserRole role;
    private final Instant createdAt;

    public UserAccount(UUID id, String name, String email, String passwordHash, UserRole role, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = requireText(name, "name");
        this.email = requireText(email, "email").toLowerCase();
        this.passwordHash = requireText(passwordHash, "passwordHash");
        this.role = Objects.requireNonNull(role, "role");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public static UserAccount createOwner(String name, String email, String passwordHash) {
        return new UserAccount(UUID.randomUUID(), name, email, passwordHash, UserRole.OWNER, Instant.now());
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value.trim();
    }

    public UUID id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String email() {
        return email;
    }

    public String passwordHash() {
        return passwordHash;
    }

    public UserRole role() {
        return role;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
