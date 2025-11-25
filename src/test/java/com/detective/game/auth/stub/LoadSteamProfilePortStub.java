package com.detective.game.auth.stub;

import com.detective.game.auth.application.port.out.LoadSteamProfilePort;
import com.detective.game.auth.application.port.out.dto.SteamProfile;

public class LoadSteamProfilePortStub implements LoadSteamProfilePort {

    @Override
    public SteamProfile loadSteamProfile(String steamId) {
        return new SteamProfile(
                steamId,
                3,
                1,
                "StubUser",
                "http://profile",
                "ava",
                "full",
                "Real Name",
                "KR",
                123123L
        );
    }
}
