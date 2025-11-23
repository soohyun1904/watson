package com.detective.game.auth.infrastructure.steam.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * Steam API 응답 래퍼
 * API: ISteamUser/GetPlayerSummaries/v2
 */
@Getter
@NoArgsConstructor
@ToString
public class SteamApiResponse {

    private Response response;

    @Getter
    @NoArgsConstructor
    @ToString
    public static class Response {
        private List<SteamPlayerSummary> players;
    }
}
