package com.detective.game.auth.infrastructure.steam.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Steam 사용자 정보
 * API: ISteamUser/GetPlayerSummaries/v2
 */
@Getter
@NoArgsConstructor
@ToString
public class SteamPlayerSummary {

    @JsonProperty("steamid")
    private String steamId;

    @JsonProperty("communityvisibilitystate")
    private Integer communityVisibilityState;

    @JsonProperty("profilestate")
    private Integer profileState;

    @JsonProperty("personaname")
    private String personaName;

    @JsonProperty("profileurl")
    private String profileUrl;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("avatarmedium")
    private String avatarMedium;

    @JsonProperty("avatarfull")
    private String avatarFull;

    @JsonProperty("avatarhash")
    private String avatarHash;

    @JsonProperty("personastate")
    private Integer personaState;

    @JsonProperty("realname")
    private String realName;

    @JsonProperty("timecreated")
    private Long timeCreated;

    @JsonProperty("loccountrycode")
    private String locCountryCode;

    @JsonProperty("locstatecode")
    private String locStateCode;
}
