package com.detective.game.auth.application.command;

import com.detective.game.common.exception.SteamException;
import static com.detective.game.common.exception.ErrorMessage.AUTH_OPENID_REQUIRED_PARAM_MISSING;

public record LogoutCommand(Long userId) {
    public LogoutCommand {
        if (userId == null) {
            throw new SteamException(AUTH_OPENID_REQUIRED_PARAM_MISSING);
        }
    }
}
