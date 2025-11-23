package com.detective.game.auth.adapter.out.external;

import com.detective.game.auth.application.port.out.LoadSteamProfilePort;
import com.detective.game.auth.application.port.out.dto.SteamProfile;
import com.detective.game.auth.infrastructure.steam.dto.SteamPlayerSummary;
import com.detective.game.auth.infrastructure.steam.SteamApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SteamApiClientAdapter implements LoadSteamProfilePort {

    private final SteamApiClient steamApiClient;

    @Override
    public SteamProfile loadSteamProfile(String steamId) {
        SteamPlayerSummary s = steamApiClient.getPlayerSummary(steamId);

        return new SteamProfile(
                s.getSteamId(),
                s.getCommunityVisibilityState(),
                s.getPersonaState(),
                s.getPersonaName(),
                s.getProfileUrl(),
                s.getAvatarMedium(),
                s.getAvatarFull(),
                s.getRealName(),
                s.getLocCountryCode(),
                s.getTimeCreated()
        );
    }
}
