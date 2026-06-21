package com.lari.finance.api.infrastructure.security;

import com.lari.finance.api.domain.model.UserAccount;
import com.lari.finance.api.domain.port.UserAccountRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecurityUserDetailsService implements UserDetailsService {
    private final UserAccountRepository userAccountRepository;

    public SecurityUserDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount user = userAccountRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado."));
        return new User(
            user.email(),
            user.passwordHash(),
            List.of(new SimpleGrantedAuthority("ROLE_" + user.role().name()))
        );
    }
}
