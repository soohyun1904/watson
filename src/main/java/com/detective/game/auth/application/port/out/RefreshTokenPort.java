package com.detective.game.auth.application.port.out;

import com.detective.game.auth.domain.model.AuthUser;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenPort {
    boolean validate(String token);

    Optional<AuthUser> getUserByRefreshToken(String token);

    void save(String token, Long userId, Instant expiryDate);

    void deleteByUserId(Long userId);
}
