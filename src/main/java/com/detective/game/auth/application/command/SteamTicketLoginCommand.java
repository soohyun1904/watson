package com.detective.game.auth.application.command;

import com.detective.game.common.exception.AuthException;
import com.detective.game.common.exception.ErrorMessage;

public record SteamTicketLoginCommand(String authTicket) {
    public SteamTicketLoginCommand {
        if (authTicket == null || authTicket.isBlank()) {
            throw new AuthException(ErrorMessage.AUTH_TICKET_REQUIRED);
        }
    }
}
