package com.detective.game.auth.application.port.out;

import com.detective.game.steam.dto.SteamPlayerSummary;

public interface LoadSteamProfilePort {
    SteamPlayerSummary loadSteamProfile(String steamId);
}
