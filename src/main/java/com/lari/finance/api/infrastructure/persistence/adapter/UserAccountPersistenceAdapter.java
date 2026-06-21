package com.lari.finance.api.infrastructure.persistence.adapter;

import com.lari.finance.api.domain.model.UserAccount;
import com.lari.finance.api.domain.port.UserAccountRepository;
import com.lari.finance.api.infrastructure.persistence.mapper.UserAccountMapper;
import com.lari.finance.api.infrastructure.persistence.repository.JpaUserAccountRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("!memory")
public class UserAccountPersistenceAdapter implements UserAccountRepository {
    private final JpaUserAccountRepository repository;

    public UserAccountPersistenceAdapter(JpaUserAccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserAccount save(UserAccount user) {
        return UserAccountMapper.toDomain(repository.save(UserAccountMapper.toEntity(user)));
    }

    @Override
    public Optional<UserAccount> findByEmail(String email) {
        return repository.findByEmail(email).map(UserAccountMapper::toDomain);
    }

    @Override
    public Optional<UserAccount> findById(UUID id) {
        return repository.findById(id).map(UserAccountMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
}
