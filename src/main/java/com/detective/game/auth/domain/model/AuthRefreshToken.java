package com.detective.game.auth.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.time.Instant;

@Getter
@RequiredArgsConstructor
public class AuthRefreshToken {
    private final Long userId;
    private final String token;
    private final Instant expiry;
    private final boolean revoked;

    public boolean isExpired() {
        return Instant.now().isAfter(expiry);
    }
}
