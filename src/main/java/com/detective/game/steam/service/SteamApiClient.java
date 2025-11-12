package com.detective.game.steam.service;

import com.detective.game.common.exception.SteamException;
import com.detective.game.steam.dto.SteamApiResponse;
import com.detective.game.steam.dto.SteamPlayerSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.time.Duration;
import static com.detective.game.common.exception.ErrorMessage.*;

/**
 * Steam Web API 클라이언트
 * ISteamUser/GetPlayerSummaries/v2 API 호출
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SteamApiClient {

    private final WebClient webClient;

    @Value("${steam.api.key}")
    private String steamApiKey;

    @Value("${steam.api.base-url}")
    private String baseUrl;

    /**
     * Steam API를 통해 사용자 정보 조회
     *
     * @param steamId Steam 고유 ID (17자리 숫자)
     * @return Steam 사용자 정보
     * @throws SteamException Steam API 호출 실패 시
     */
    public SteamPlayerSummary getPlayerSummary(String steamId) {
        log.debug("Steam API 호출 시작: steamId={}", steamId);

        String url = String.format(
                "%s/ISteamUser/GetPlayerSummaries/v2/?key=%s&steamids=%s",
                baseUrl, steamApiKey, steamId
        );

        try {
            SteamApiResponse response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(SteamApiResponse.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();

            // 응답 검증
            if (response == null ||
                    response.getResponse() == null ||
                    response.getResponse().getPlayers() == null ||
                    response.getResponse().getPlayers().isEmpty()) {

                log.error("Steam API 응답이 비어있음: steamId={}", steamId);
                throw new SteamException(STEAM_USER_NOT_FOUND);
            }

            SteamPlayerSummary summary = response.getResponse().getPlayers().get(0);

            log.info("Steam 사용자 정보 조회 성공: steamId={}, personaName={}",
                    steamId, summary.getPersonaName());

            return summary;

        } catch (WebClientResponseException e) {
            log.error("Steam API HTTP 오류: steamId={}, status={}, body={}",
                    steamId, e.getStatusCode(), e.getResponseBodyAsString());

            if (e.getStatusCode().is4xxClientError()) {
                throw new SteamException(STEAM_INVALID_API_KEY);
            }
            throw new SteamException(STEAM_API_REQUEST_FAILED);

        } catch (Exception e) {
            log.error("Steam API 호출 실패: steamId={}, error={}",
                    steamId, e.getMessage(), e);

            if (e.getMessage() != null && e.getMessage().contains("timeout")) {
                throw new SteamException(STEAM_API_TIMEOUT);
            }
            throw new SteamException(STEAM_API_REQUEST_FAILED);
        }
    }

    /**
     * 여러 사용자 정보를 한 번에 조회 (최대 100명)
     *
     * @param steamIds Steam ID 목록 (쉼표로 구분)
     * @return Steam 사용자 정보 목록
     */
    public java.util.List<SteamPlayerSummary> getPlayerSummaries(String steamIds) {
        log.debug("Steam API 일괄 조회 시작: steamIds={}", steamIds);

        String url = String.format(
                "%s/ISteamUser/GetPlayerSummaries/v2/?key=%s&steamids=%s",
                baseUrl, steamApiKey, steamIds
        );

        try {
            SteamApiResponse response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(SteamApiResponse.class)
                    .timeout(Duration.ofSeconds(15))
                    .block();

            if (response == null ||
                    response.getResponse() == null ||
                    response.getResponse().getPlayers() == null) {
                return java.util.Collections.emptyList();
            }

            log.info("Steam 사용자 정보 일괄 조회 성공: count={}",
                    response.getResponse().getPlayers().size());

            return response.getResponse().getPlayers();

        } catch (Exception e) {
            log.error("Steam API 일괄 조회 실패: error={}", e.getMessage(), e);
            throw new SteamException(STEAM_API_REQUEST_FAILED);
        }
    }
}
