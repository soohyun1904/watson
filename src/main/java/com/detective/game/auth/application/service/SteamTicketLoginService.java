package com.detective.game.auth.application.service;

import com.detective.game.auth.application.command.SteamTicketLoginCommand;
import com.detective.game.auth.application.port.in.SteamLoginResult;
import com.detective.game.auth.application.port.in.SteamTicketLoginUseCase;
import com.detective.game.auth.application.port.out.*;
import com.detective.game.auth.application.port.out.dto.SteamProfile;
import com.detective.game.auth.application.port.out.dto.TokenPair;
import com.detective.game.auth.domain.model.AuthUser;
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
public class SteamTicketLoginService implements SteamTicketLoginUseCase {

    private final SteamTicketVerifyPort steamTicketVerifyPort;
    private final LoadSteamProfilePort loadSteamProfilePort;
    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final JwtPort jwtPort;
    private final RefreshTokenPort refreshTokenPort;

    @Override
    public SteamLoginResult loginWithTicket(SteamTicketLoginCommand command) {

        String steamId = steamTicketVerifyPort.verifyTicketAndGetSteamId(command.authTicket());
        log.info("Steam AuthTicket 검증 성공: steamId={}", steamId);

        SteamProfile profile = loadSteamProfilePort.loadSteamProfile(steamId);
        log.info("Steam 프로필 조회 성공: {}", profile.getPersonaName());

        AuthUser user = loadUserPort.findBySteamId(steamId)
                .map(existing -> updateUser(existing, profile))
                .orElseGet(() -> createUser(profile));

        TokenPair tokens = jwtPort.generateTokens(
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
    }


    private AuthUser createUser(SteamProfile profile) {
        AuthUser newUser = new AuthUser(
                null,
                profile.getSteamId(),
                Role.USER,
                profile.getPersonaName(),
                profile.getProfileUrl(),
                profile.getAvatarMedium(),
                profile.getAvatarFull(),
                profile.getRealName(),
                profile.getCountryCode(),
                true,
                ProfileVisibility.fromCode(profile.getCommunityVisibilityState()),
                PersonaState.fromCode(profile.getPersonaState()),
                convert(profile.getTimeCreated()),
                LocalDateTime.now(),
                1
        );
        return saveUserPort.save(newUser);
    }

    private AuthUser updateUser(AuthUser existing, SteamProfile profile) {
        AuthUser updated = new AuthUser(
                existing.getId(),
                existing.getSteamId(),
                existing.getRole(),
                profile.getPersonaName(),
                profile.getProfileUrl(),
                profile.getAvatarMedium(),
                profile.getAvatarFull(),
                profile.getRealName(),
                profile.getCountryCode(),
                existing.getActive(),
                ProfileVisibility.fromCode(profile.getCommunityVisibilityState()),
                PersonaState.fromCode(profile.getPersonaState()),
                existing.getTimeCreated(),
                LocalDateTime.now(),
                existing.getLoginCount() + 1
        );
        return saveUserPort.save(updated);
    }

    private LocalDateTime convert(Long unixTime) {
        if (unixTime == null) return null;
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(unixTime), ZoneId.systemDefault());
    }
}
