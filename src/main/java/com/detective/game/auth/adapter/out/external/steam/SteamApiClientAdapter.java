package com.detective.game.auth.adapter.out.external.steam;

import com.detective.game.auth.application.port.out.LoadSteamProfilePort;
import com.detective.game.steam.dto.SteamPlayerSummary;
import com.detective.game.steam.service.SteamApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SteamApiClientAdapter implements LoadSteamProfilePort {

    private final SteamApiClient steamApiClient;

    @Override
    public SteamPlayerSummary loadSteamProfile(String steamId) {
        return steamApiClient.getPlayerSummary(steamId);
    }
}
