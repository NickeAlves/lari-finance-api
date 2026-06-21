package com.lari.finance.api.application.service;

import com.lari.finance.api.application.exception.NotFoundException;
import com.lari.finance.api.domain.model.UserAccount;
import com.lari.finance.api.domain.port.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CurrentUserService {
    private final UserAccountRepository userAccountRepository;

    public CurrentUserService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional(readOnly = true)
    public UserAccount getByEmail(String email) {
        return userAccountRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("Usuario no encontrado."));
    }
}
