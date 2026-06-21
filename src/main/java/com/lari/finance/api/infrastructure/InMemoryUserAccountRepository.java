package com.lari.finance.api.infrastructure;

import com.lari.finance.api.domain.model.UserAccount;
import com.lari.finance.api.domain.port.UserAccountRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
@Profile("memory")
public class InMemoryUserAccountRepository implements UserAccountRepository {
	private final ConcurrentMap<UUID, UserAccount> usersById = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, UUID> idsByEmail = new ConcurrentHashMap<>();

	@Override
	public UserAccount save(UserAccount user) {
		usersById.put(user.id(), user);
		idsByEmail.put(user.email(), user.id());
		return user;
	}

	@Override
	public Optional<UserAccount> findByEmail(String email) {
		UUID id = idsByEmail.get(email == null ? "" : email.toLowerCase());
		return id == null ? Optional.empty() : findById(id);
	}

	@Override
	public Optional<UserAccount> findById(UUID id) {
		return Optional.ofNullable(usersById.get(id));
	}

	@Override
	public boolean existsByEmail(String email) {
		return idsByEmail.containsKey(email == null ? "" : email.toLowerCase());
	}
}
