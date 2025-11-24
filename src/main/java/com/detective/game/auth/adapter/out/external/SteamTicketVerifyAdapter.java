package com.detective.game.auth.adapter.out.external;

import com.detective.game.auth.application.port.out.SteamTicketVerifyPort;
import com.detective.game.common.exception.AuthException;
import com.detective.game.auth.infrastructure.steam.dto.SteamTicketAuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import static com.detective.game.common.exception.ErrorMessage.AUTH_INVALID_TOKEN;

@Slf4j
@Component
@RequiredArgsConstructor
public class SteamTicketVerifyAdapter implements SteamTicketVerifyPort {

    private final WebClient webClient;

    @Value("${steam.api.key}")
    private String steamApiKey;

    @Value("${steam.app-id}")
    private String appId;

    private static final String STEAM_AUTH_URL =
            "https://api.steampowered.com/ISteamUserAuth/AuthenticateUserTicket/v1/";

    @Override
    public String verifyTicketAndGetSteamId(String authTicket) {

        try {
            log.info("Steam AuthTicket 검증 요청 시작");

            SteamTicketAuthResponse response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(STEAM_AUTH_URL)
                            .queryParam("key", steamApiKey)
                            .queryParam("appid", appId)
                            .queryParam("ticket", authTicket)
                            .build())
                    .retrieve()
                    .bodyToMono(SteamTicketAuthResponse.class)
                    .block();

            if (response == null ||
                    response.getResponse() == null ||
                    response.getResponse().getParams() == null ||
                    response.getResponse().getParams().getSteamId() == null) {

                log.error("Steam AuthTicket 검증 실패: 응답 없음");
                throw new AuthException(AUTH_INVALID_TOKEN);
            }

            String steamId = response.getResponse().getParams().getSteamId();

            log.info("Steam AuthTicket 검증 성공: steamId={}", steamId);
            return steamId;

        } catch (Exception e) {
            log.error("Steam AuthTicket 검증 중 오류: {}", e.getMessage());
            throw new AuthException(AUTH_INVALID_TOKEN);
        }
    }
}

