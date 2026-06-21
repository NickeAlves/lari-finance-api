package com.lari.finance.api.application.dto;

import com.lari.finance.api.domain.model.UserAccount;

public record AuthResult(String token, UserAccount user) {
}
