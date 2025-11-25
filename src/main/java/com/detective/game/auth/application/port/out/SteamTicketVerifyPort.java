package com.detective.game.auth.application.port.out;

public interface SteamTicketVerifyPort {
    String verifyTicketAndGetSteamId(String authTicket);
}
