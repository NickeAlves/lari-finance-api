package com.lari.finance.api.infrastructure.persistence.mapper;

import com.lari.finance.api.domain.model.UserAccount;
import com.lari.finance.api.infrastructure.persistence.entity.UserAccountEntity;

public final class UserAccountMapper {
    private UserAccountMapper() {
    }

    public static UserAccount toDomain(UserAccountEntity entity) {
        return new UserAccount(
            entity.getId(),
            entity.getName(),
            entity.getEmail(),
            entity.getPasswordHash(),
            entity.getRole(),
            entity.getCreatedAt()
        );
    }

    public static UserAccountEntity toEntity(UserAccount user) {
        return new UserAccountEntity(
            user.id(),
            user.name(),
            user.email(),
            user.passwordHash(),
            user.role(),
            user.createdAt()
        );
    }
}
