package com.detective.game.auth.application.port.in;

import com.detective.game.auth.application.command.SteamTicketLoginCommand;

public interface SteamTicketLoginUseCase {
    SteamLoginResult loginWithTicket(SteamTicketLoginCommand command);
}
