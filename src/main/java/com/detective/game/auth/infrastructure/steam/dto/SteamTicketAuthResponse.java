package com.detective.game.auth.infrastructure.steam.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SteamTicketAuthResponse {

    @JsonProperty("response")
    private Response response;

    @Getter
    @NoArgsConstructor
    public static class Response {
        @JsonProperty("params")
        private Params params;
    }

    @Getter
    @NoArgsConstructor
    public static class Params {
        @JsonProperty("steamid")
        private String steamId;

        @JsonProperty("ownersteamid")
        private String ownerSteamId;

        @JsonProperty("vacbanned")
        private boolean vacBanned;

        @JsonProperty("publisherbanned")
        private boolean publisherBanned;
    }
}
