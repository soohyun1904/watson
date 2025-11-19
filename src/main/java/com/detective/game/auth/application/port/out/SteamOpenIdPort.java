package com.detective.game.auth.application.port.out;

import java.util.Map;

public interface SteamOpenIdPort {
    String validateAndExtractSteamId(Map<String, String> params);
}
