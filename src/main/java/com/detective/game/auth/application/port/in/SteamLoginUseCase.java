package com.detective.game.auth.application.port.in;

import com.detective.game.auth.application.command.SteamLoginCommand;

public interface SteamLoginUseCase {
    SteamLoginResult login(SteamLoginCommand command);
}
