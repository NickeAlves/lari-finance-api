package com.lari.finance.api.domain.port;

import com.lari.finance.api.domain.model.UserAccount;

public interface TokenService {
    String generate(UserAccount user);

    String subject(String token);

    boolean isValid(String token);
}
