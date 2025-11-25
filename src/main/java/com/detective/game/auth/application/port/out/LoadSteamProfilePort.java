package com.detective.game.auth.application.port.out;

import com.detective.game.auth.application.port.out.dto.SteamProfile;

public interface LoadSteamProfilePort {
    SteamProfile loadSteamProfile(String steamId);
}
