package com.detective.game.auth.stub;

import com.detective.game.auth.application.port.out.SteamTicketVerifyPort;

public class SteamTicketVerifyPortStub implements SteamTicketVerifyPort {

    @Override
    public String verifyTicketAndGetSteamId(String authTicket) {
        return "76561198000000000"; // 테스트용 SteamID
    }
}
