package com.detective.game.auth.application.service;

import com.detective.game.auth.application.command.RefreshTokenCommand;
import com.detective.game.auth.application.port.in.AccessTokenResult;
import com.detective.game.auth.application.port.in.RefreshTokenUseCase;
import com.detective.game.auth.application.port.out.JwtPort;
import com.detective.game.auth.application.port.out.RefreshTokenPort;
import com.detective.game.auth.domain.model.AuthUser;
import com.detective.game.common.exception.AuthException;
import com.detective.game.common.exception.ErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenUseCase {
    private final RefreshTokenPort refreshTokenPort;
    private final JwtPort jwtPort;

    @Override
    public AccessTokenResult refresh(RefreshTokenCommand command) {
        String token = command.refreshToken();

        if (!refreshTokenPort.validate(token)) {
            throw new AuthException(ErrorMessage.AUTH_INVALID_REFRESH_TOKEN);
        }

        AuthUser user = refreshTokenPort.getUserByRefreshToken(token)
                .orElseThrow(() -> new AuthException(ErrorMessage.USER_NOT_FOUND));

        String accessToken = jwtPort.generateAccessToken(
                user.getSteamId(),
                user.getId(),
                user.getRole().name()
        );

        return AccessTokenResult.builder()
                .accessToken(accessToken)
                .build();
    }
}
