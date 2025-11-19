package com.detective.game.auth.adapter.in.web;

import com.detective.game.auth.application.command.SteamLoginCommand;
import com.detective.game.auth.application.port.in.SteamLoginResult;
import com.detective.game.auth.application.port.in.SteamLoginUseCase;
import com.detective.game.common.exception.AuthException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/auth/steam")
@RequiredArgsConstructor
@Tag(name = "Steam Authentication", description = "Steam OpenID 인증 API")
public class SteamAuthController {
    private final SteamLoginUseCase steamLoginUseCase;

    @Value("${steam.openid.realm}")
    private String openIdRealm;

    @Value("${steam.openid.return-url}")
    private String openIdReturnUrl;

    @Value("${game.client.success-redirect}")
    private String successRedirect;

    @Value("${game.client.failure-redirect}")
    private String failureRedirect;

    @GetMapping("/login")
    @Operation(summary = "Steam 로그인",
            description = "Steam OpenID 로그인 페이지로 리다이렉트합니다.")
    public void login(HttpServletResponse response) throws IOException {
        log.info("Steam 로그인 요청");

        // OpenID 파라미터 구성
        Map<String, String> params = Map.of(
                "openid.ns", "http://specs.openid.net/auth/2.0",
                "openid.mode", "checkid_setup",
                "openid.return_to", openIdReturnUrl,
                "openid.realm", openIdRealm,
                "openid.identity", "http://specs.openid.net/auth/2.0/identifier_select",
                "openid.claimed_id", "http://specs.openid.net/auth/2.0/identifier_select"
        );

        // URL 쿼리 문자열 생성
        String queryString = params.entrySet().stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        String steamLoginUrl = "https://steamcommunity.com/openid/login?" + queryString;

        log.debug("Steam OpenID URL로 리다이렉트: {}", steamLoginUrl);
        response.sendRedirect(steamLoginUrl);
    }

    @GetMapping("/callback")
    @Operation(summary = "Steam 로그인",
            description = "Steam OpenID 로그인 페이지로 리다이렉트합니다.")
    public void callback(@RequestParam Map<String, String> params,
                         HttpServletResponse response) throws IOException {

        try {
            SteamLoginResult result = steamLoginUseCase.login(
                    new SteamLoginCommand(params)
            );

            String redirectUrl = String.format(
                    "%s?accessToken=%s&refreshToken=%s&userId=%d",
                    successRedirect,
                    URLEncoder.encode(result.getAccessToken(), StandardCharsets.UTF_8),
                    URLEncoder.encode(result.getRefreshToken(), StandardCharsets.UTF_8),
                    result.getUserId()
            );

            response.sendRedirect(redirectUrl);

        } catch (AuthException e) {
            handleAuthError(response, e.getMessage());
        } catch (Exception e) {
            handleAuthError(response, "로그인 처리 중 오류가 발생했습니다.");
        }
    }

    private void handleAuthError(HttpServletResponse response, String errorMessage) throws IOException {
        String redirectUrl = String.format(
                "%s?error=%s",
                failureRedirect,
                URLEncoder.encode(errorMessage, StandardCharsets.UTF_8)
        );
        response.sendRedirect(redirectUrl);
    }
}
