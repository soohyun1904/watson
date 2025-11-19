package com.detective.game.auth.application.port.in;

import com.detective.game.auth.application.command.LogoutCommand;

public interface LogoutUseCase {
    void logout(LogoutCommand command);
}
