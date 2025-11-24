package com.detective.game.auth.stub;

import com.detective.game.auth.application.port.out.RefreshTokenPort;
import com.detective.game.auth.domain.model.AuthUser;

import java.time.Instant;
import java.util.Optional;

public class RefreshTokenPortStub implements RefreshTokenPort {

    @Override
    public boolean validate(String token) {
        return true;
    }

    @Override
    public Optional<AuthUser> getUserByRefreshToken(String token) {
        return Optional.empty();
    }

    @Override
    public void save(String token, Long userId, Instant expiryDate) { }

    @Override
    public void deleteByUserId(Long userId) { }
}
