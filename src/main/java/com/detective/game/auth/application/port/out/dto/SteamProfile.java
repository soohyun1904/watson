package com.detective.game.auth.application.port.out.dto;

import lombok.Value;

@Value
public class SteamProfile {
    String steamId;
    Integer communityVisibilityState;
    Integer personaState;
    String personaName;
    String profileUrl;
    String avatarMedium;
    String avatarFull;
    String realName;
    String countryCode;
    Long timeCreated; // Unix time (초 단위)
}
