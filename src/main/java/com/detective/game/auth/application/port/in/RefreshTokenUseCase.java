package com.detective.game.auth.application.port.in;

import com.detective.game.auth.application.command.RefreshTokenCommand;

public interface RefreshTokenUseCase {
    AccessTokenResult refresh(RefreshTokenCommand command);
}
