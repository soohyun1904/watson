package com.detective.game.auth.application.port.out;

import com.detective.game.auth.application.port.out.dto.TokenPair;
import java.util.Date;
import java.util.Optional;

public interface JwtPort {
    TokenPair generateTokens(String steamId, Long userId, String role);

    String generateAccessToken(String steamId, Long userId, String role);

    void validateToken(String token);

    boolean isAccessToken(String token);

    boolean isRefreshToken(String token);

    String getSteamIdFromToken(String token);

    Long getUserIdFromToken(String token);

    Optional<String> getRoleFromToken(String token);

    Date getExpirationFromToken(String token);
}
