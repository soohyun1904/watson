package com.detective.game.auth.infrastructure.jwt;

import com.detective.game.auth.adapter.out.security.UserDetailsImpl;
import com.detective.game.common.exception.AuthException;
import com.detective.game.auth.adapter.out.security.dto.TokenDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.detective.game.common.exception.ErrorMessage.*;

@Slf4j
@Component
public class JwtTokenProvider {
    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration.validity-in-minutes}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration.validity-in-days}") long refreshTokenExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    //Access Token 생성 (12시간)
    public String generateAccessToken(String steamId, Long userId, String role) {
        Instant now = Instant.now();
        Instant expiration = now.plus(accessTokenExpiration, ChronoUnit.MINUTES);

        return Jwts.builder().subject(steamId)
                .claim("userId", userId)
                .claim("role", role)
                .claim("type", "access")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    //Refresh Token 생성 (14일)
    public String generateRefreshToken(String steamId) {
        Instant now = Instant.now();
        Instant expiration = now.plus(refreshTokenExpiration, ChronoUnit.DAYS);

        return Jwts.builder().subject(steamId)
                .claim("type", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Long userId = getUserIdFromToken(token);
        String steamId = getSteamIdFromToken(token);
        String role = getRoleFromToken(token).orElse("ROLE_USER");

        UserDetailsImpl userDetails = new UserDetailsImpl(
                userId,
                steamId,
                steamId, // username은 steamId로 사용
                List.of(new SimpleGrantedAuthority(role))
        );

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    // 토큰에서 Steam ID 추출
    public String getSteamIdFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    // 토큰에서 User ID 추출
    public Long getUserIdFromToken(String token) {
        return getClaimsFromToken(token).get("userId", Long.class);
    }

    //토큰에서 role 추출
    public Optional<String> getRoleFromToken(String token){
        return Optional.ofNullable(getClaimsFromToken(token).get("role", String.class));
    }

    //토큰 만료 시간 확인
    public Date getExpirationFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    //토큰 유효형 검증
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (ExpiredJwtException e){
            log.error("event=jwt_token_expired, token={}", token.substring(0, Math.min(token.length(), 20)) + "...");
            throw new AuthException(JWT_EXPIRED);
        } catch (MalformedJwtException e) {
            log.error("event=jwt_token_malformed, error_message={}", e.getMessage());
            throw new AuthException(JWT_MALFORMED);
        } catch (UnsupportedJwtException e) {
            log.error("event=jwt_token_unsupported, error_message={}", e.getMessage());
            throw new AuthException(JWT_UNSUPPORTED);
        } catch (IllegalArgumentException e) {
            log.error("event=jwt_token_claims_empty, error_message={}", e.getMessage());
            throw new AuthException(JWT_CLAIMS_EMPTY);
        } catch (JwtException e) {
            log.error("event=jwt_token_invalid_signature, error_message={}", e.getMessage());
            throw new AuthException(JWT_INVALID_SIGNATURE);
        }
    }

    // Access Token인지 확인
    public boolean isAccessToken(String token) {
        return "access".equals(getTokenType(token));
    }

    // Refresh Token인지 확인
    public boolean isRefreshToken(String token) {
        return "refresh".equals(getTokenType(token));
    }

    // 토큰 타입 확인 (access or refresh)
    public String getTokenType(String token) {
        return getClaimsFromToken(token).get("type", String.class);
    }

    // 토큰에서 Claims 추출
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
