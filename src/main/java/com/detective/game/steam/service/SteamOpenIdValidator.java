package com.detective.game.steam.service;

import com.detective.game.common.exception.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.detective.game.common.exception.ErrorMessage.*;

/**
 * Steam OpenID 2.0 검증기
 * Steam 로그인 콜백 파라미터를 검증하고 Steam ID를 추출
 */
@Slf4j
@Component
public class SteamOpenIdValidator {

    private static final String STEAM_OPENID_URL = "https://steamcommunity.com/openid/login";
    private static final Pattern STEAM_ID_PATTERN =
            Pattern.compile("^https?://steamcommunity\\.com/openid/id/(7656119\\d{10})/?$");

    // OpenID 필수 파라미터
    private static final String[] REQUIRED_PARAMS = {
            "openid.ns",
            "openid.op_endpoint",
            "openid.claimed_id",
            "openid.identity",
            "openid.return_to",
            "openid.response_nonce",
            "openid.assoc_handle",
            "openid.signed",
            "openid.sig"
    };

    /**
     * Steam OpenID 검증 및 Steam ID 추출
     *
     * @param params Steam 콜백 파라미터 (openid.*)
     * @return 검증된 Steam ID (17자리 숫자)
     * @throws AuthException 검증 실패 시
     */
    public String validateAndExtractSteamId(Map<String, String> params) {
        log.debug("Steam OpenID 검증 시작: params={}", params.keySet());

        // 1. 필수 파라미터 확인
        validateRequiredParams(params);

        // 2. Steam 서버에 검증 요청
        validateWithSteamServer(params);

        // 3. Steam ID 추출
        String steamId = extractSteamId(params.get("openid.identity"));

        log.info("Steam OpenID 검증 성공: steamId={}", steamId);
        return steamId;
    }

    /**
     * 필수 OpenID 파라미터 존재 여부 확인
     */
    private void validateRequiredParams(Map<String, String> params) {
        for (String param : REQUIRED_PARAMS) {
            if (!params.containsKey(param) || params.get(param) == null || params.get(param).isEmpty()) {
                log.error("필수 OpenID 파라미터 누락: {}", param);
                throw new AuthException(AUTH_OPENID_REQUIRED_PARAM_MISSING);
            }
        }

        // openid.ns 값 검증
        if (!"http://specs.openid.net/auth/2.0".equals(params.get("openid.ns"))) {
            log.error("잘못된 openid.ns 값: {}", params.get("openid.ns"));
            throw new AuthException(AUTH_OPENID_INVALID_PARAMS);
        }
    }

    /**
     * Steam 서버에 OpenID 검증 요청
     */
    private void validateWithSteamServer(Map<String, String> params) {
        log.debug("Steam 서버에 검증 요청 전송");

        // OpenID 파라미터 복사 및 mode 변경
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        params.forEach(requestParams::add);
        requestParams.set("openid.mode", "check_authentication");

        // HTTP 요청 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(requestParams, headers);

        RestTemplate restTemplate = new RestTemplate();

        try {
            // Steam 서버에 검증 요청
            ResponseEntity<String> response = restTemplate.postForEntity(
                    STEAM_OPENID_URL,
                    request,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Steam OpenID 검증 HTTP 오류: status={}", response.getStatusCode());
                throw new AuthException(AUTH_OPENID_VALIDATION_FAILED);
            }

            // 응답 파싱
            Map<String, String> responseBody = parseResponse(response.getBody());
            log.debug("Steam 검증 응답: {}", responseBody);

            // 검증 결과 확인
            if (!"http://specs.openid.net/auth/2.0".equals(responseBody.get("ns"))) {
                log.error("잘못된 OpenID 네임스페이스: {}", responseBody.get("ns"));
                throw new AuthException(AUTH_OPENID_VALIDATION_FAILED);
            }

            if (!"true".equals(responseBody.get("is_valid"))) {
                log.error("Steam OpenID 검증 실패: is_valid={}", responseBody.get("is_valid"));
                throw new AuthException(AUTH_OPENID_VALIDATION_FAILED);
            }

            log.debug("Steam 서버 검증 성공");

        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            log.error("Steam OpenID 검증 중 예외 발생: {}", e.getMessage(), e);
            throw new AuthException(AUTH_OPENID_VALIDATION_FAILED);
        }
    }

    /**
     * openid.identity에서 Steam ID 추출
     *
     * @param identity openid.identity 값 (예: https://steamcommunity.com/openid/id/76561198012345678)
     * @return Steam ID (17자리 숫자)
     */
    private String extractSteamId(String identity) {
        if (identity == null || identity.isEmpty()) {
            log.error("openid.identity가 비어있음");
            throw new AuthException(AUTH_INVALID_STEAM_ID);
        }

        Matcher matcher = STEAM_ID_PATTERN.matcher(identity);

        if (!matcher.find()) {
            log.error("잘못된 Steam ID 형식: identity={}", identity);
            throw new AuthException(AUTH_INVALID_STEAM_ID);
        }

        String steamId = matcher.group(1);
        log.debug("Steam ID 추출 성공: steamId={}", steamId);

        return steamId;
    }

    /**
     * Steam 응답 파싱 (key:value 형식)
     *
     * @param response Steam 서버 응답 본문
     * @return 파싱된 key-value 맵
     */
    private Map<String, String> parseResponse(String response) {
        Map<String, String> result = new HashMap<>();

        if (response == null || response.isEmpty()) {
            return result;
        }

        for (String line : response.split("\n")) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split(":", 2);
            if (parts.length == 2) {
                result.put(parts[0], parts[1]);
            }
        }

        return result;
    }
}
