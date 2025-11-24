package com.detective.game.auth.application.command;

import com.detective.game.common.exception.RefreshTokenException;

import static com.detective.game.common.exception.ErrorMessage.AUTH_REFRESH_TOKEN_NOT_FOUND;

public record RefreshTokenCommand(String refreshToken) {
    public RefreshTokenCommand {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new RefreshTokenException(AUTH_REFRESH_TOKEN_NOT_FOUND);
        }
    }
}