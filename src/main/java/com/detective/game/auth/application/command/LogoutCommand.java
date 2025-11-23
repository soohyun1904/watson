package com.detective.game.auth.application.command;

import com.detective.game.common.exception.AuthException;
import static com.detective.game.common.exception.ErrorMessage.USER_ID_EMPTY;

public record LogoutCommand(Long userId) {
    public LogoutCommand {
        if (userId == null) {
            throw new AuthException(USER_ID_EMPTY);
        }
    }
}
