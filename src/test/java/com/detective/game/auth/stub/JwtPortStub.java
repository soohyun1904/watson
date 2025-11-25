package com.detective.game.auth.stub;

import com.detective.game.auth.application.port.out.JwtPort;
import com.detective.game.auth.application.port.out.dto.TokenPair;

import java.util.Date;
import java.util.Optional;

public class JwtPortStub implements JwtPort {

    @Override
    public TokenPair generateTokens(String steamId, Long userId, String role) {
        return new TokenPair("ACCESS_TOKEN", "REFRESH_TOKEN");
    }

    @Override
    public String generateAccessToken(String steamId, Long userId, String role) {
        return "ACCESS_TOKEN";
    }

    @Override
    public void validateToken(String token) {
        // stub: always valid
    }

    @Override
    public boolean isAccessToken(String token) {
        return token.equals("ACCESS_TOKEN");
    }

    @Override
    public boolean isRefreshToken(String token) {
        return token.equals("REFRESH_TOKEN");
    }

    @Override
    public String getSteamIdFromToken(String token) {
        return "76561198000000000";
    }

    @Override
    public Long getUserIdFromToken(String token) {
        return 1L;
    }

    @Override
    public Optional<String> getRoleFromToken(String token) {
        return Optional.of("USER");
    }

    @Override
    public Date getExpirationFromToken(String token) {
        return new Date();
    }
}
