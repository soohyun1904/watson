package com.detective.game.auth.adapter.out.security;

import com.detective.game.auth.application.port.out.JwtPort;
import com.detective.game.auth.application.port.out.dto.TokenPair;
import com.detective.game.auth.infrastructure.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class JwtTokenProviderAdapter implements JwtPort {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public TokenPair generateTokens(String steamId, Long userId, String role) {
        String accessToken = jwtTokenProvider.generateAccessToken(steamId, userId, role);
        String refreshToken = jwtTokenProvider.generateRefreshToken(steamId);
        return new TokenPair(accessToken, refreshToken);
    }

    @Override
    public String generateAccessToken(String steamId, Long userId, String role) {
        return jwtTokenProvider.generateAccessToken(steamId, userId, role);
    }

    @Override
    public void validateToken(String token) {
        jwtTokenProvider.validateToken(token);
    }

    @Override
    public boolean isAccessToken(String token) {
        return jwtTokenProvider.isAccessToken(token);
    }

    @Override
    public boolean isRefreshToken(String token) {
        return jwtTokenProvider.isRefreshToken(token);
    }

    @Override
    public String getSteamIdFromToken(String token) {
        return jwtTokenProvider.getSteamIdFromToken(token);
    }

    @Override
    public Long getUserIdFromToken(String token) {
        return jwtTokenProvider.getUserIdFromToken(token);
    }

    @Override
    public Optional<String> getRoleFromToken(String token) {
        return jwtTokenProvider.getRoleFromToken(token);
    }

    @Override
    public Date getExpirationFromToken(String token) {
        return jwtTokenProvider.getExpirationFromToken(token);
    }
}
