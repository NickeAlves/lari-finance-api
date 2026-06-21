package com.lari.finance.api.domain.port;

import com.lari.finance.api.domain.model.UserAccount;

import java.util.Optional;
import java.util.UUID;

public interface UserAccountRepository {
    UserAccount save(UserAccount user);

    Optional<UserAccount> findByEmail(String email);

    Optional<UserAccount> findById(UUID id);

    boolean existsByEmail(String email);
}
