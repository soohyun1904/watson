package com.detective.game.auth.application.command;

import java.util.Map;

public record SteamLoginCommand(Map<String, String> openIdParams) {
    public SteamLoginCommand {
        if (openIdParams == null || openIdParams.isEmpty()) {
            throw new IllegalArgumentException("OpenID 파라미터가 비어 있습니다.");
        }
    }
}
