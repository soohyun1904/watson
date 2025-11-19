package com.detective.game.auth.application.service;

import com.detective.game.auth.application.command.SteamLoginCommand;
import com.detective.game.auth.application.port.in.SteamLoginResult;
import com.detective.game.auth.application.port.in.SteamLoginUseCase;
import com.detective.game.auth.application.port.out.*;
import com.detective.game.auth.domain.model.AuthUser;
import com.detective.game.common.exception.AuthException;
import com.detective.game.common.exception.ErrorMessage;
import com.detective.game.steam.dto.SteamPlayerSummary;
import com.detective.game.steam.dto.TokenDTO;
import com.detective.game.user.domain.PersonaState;
import com.detective.game.user.domain.ProfileVisibility;
import com.detective.game.user.domain.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SteamLoginService implements SteamLoginUseCase {
    private final SteamOpenIdPort steamOpenIdPort;
    private final LoadSteamProfilePort loadSteamProfilePort;
    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final JwtPort jwtPort;
    private final RefreshTokenPort refreshTokenPort;

    @Override
    public SteamLoginResult login(SteamLoginCommand command) {
        try {
            String steamId = steamOpenIdPort.validateAndExtractSteamId(command.openIdParams());
            log.info("Steam ID 추출 성공: {}", steamId);

            SteamPlayerSummary profile = loadSteamProfilePort.loadSteamProfile(steamId);
            log.info("Steam 사용자 정보 조회 성공: personaName={}", profile.getPersonaName());

            AuthUser user = loadUserPort.findBySteamId(steamId)
                    .map(existing -> updateUser(existing, profile))
                    .orElseGet(() -> createUser(profile));

            TokenDTO tokens = jwtPort.generateTokens(
                    user.getSteamId(),
                    user.getId(),
                    user.getRole().name()
            );

            Instant expiry = jwtPort.getExpirationFromToken(tokens.getRefreshToken()).toInstant();
            refreshTokenPort.save(tokens.getRefreshToken(), user.getId(), expiry);

            return SteamLoginResult.builder()
                    .userId(user.getId())
                    .accessToken(tokens.getAccessToken())
                    .refreshToken(tokens.getRefreshToken())
                    .build();

        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            log.error("Steam 로그인 처리 중 예외 발생", e);
            throw new AuthException(ErrorMessage.AUTH_OPENID_VALIDATION_FAILED);
        }
    }

    private AuthUser createUser(SteamPlayerSummary summary) {
        AuthUser newUser = new AuthUser(
                null,
                summary.getSteamId(),
                Role.USER,
                summary.getPersonaName(),
                summary.getProfileUrl(),
                summary.getAvatarMedium(),
                summary.getAvatarFull(),
                summary.getRealName(),
                summary.getLocCountryCode(),
                true,
                ProfileVisibility.fromCode(summary.getCommunityVisibilityState()),
                PersonaState.fromCode(summary.getPersonaState()),
                convertToLocalDateTime(summary.getTimeCreated()),
                LocalDateTime.now(),
                1
        );
        AuthUser saved = saveUserPort.save(newUser);
        log.info("신규 사용자 생성 완료: userId={}", saved.getId());
        return saved;
    }

    private AuthUser updateUser(AuthUser user, SteamPlayerSummary summary) {
        AuthUser updated = new AuthUser(
                user.getId(),
                user.getSteamId(),
                user.getRole(),
                summary.getPersonaName(),
                summary.getProfileUrl(),
                summary.getAvatarMedium(),
                summary.getAvatarFull(),
                summary.getRealName(),
                summary.getLocCountryCode(),
                user.getActive(),
                ProfileVisibility.fromCode(summary.getCommunityVisibilityState()),
                PersonaState.fromCode(summary.getPersonaState()),
                user.getTimeCreated(),
                LocalDateTime.now(),
                user.getLoginCount() + 1
        );
        AuthUser saved = saveUserPort.save(updated);
        log.info("기존 사용자 업데이트 완료: userId={}, loginCount={}", saved.getId(), saved.getLoginCount());
        return saved;
    }

    private LocalDateTime convertToLocalDateTime(Long timestamp) {
        if (timestamp == null) return null;
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
    }
}