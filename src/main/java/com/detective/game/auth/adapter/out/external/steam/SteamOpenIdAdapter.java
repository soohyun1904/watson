package com.detective.game.auth.adapter.out.external.steam;

import com.detective.game.auth.application.port.out.SteamOpenIdPort;
import com.detective.game.steam.service.SteamOpenIdValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SteamOpenIdAdapter implements SteamOpenIdPort {

    private final SteamOpenIdValidator steamOpenIdValidator;

    @Override
    public String validateAndExtractSteamId(Map<String, String> params) {
        return steamOpenIdValidator.validateAndExtractSteamId(params);
    }
}
