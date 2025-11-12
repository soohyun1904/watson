package com.detective.game.steam.controller;


import com.detective.game.common.exception.AuthException;
import com.detective.game.common.exception.ErrorMessage;
import com.detective.game.common.response.ApiResponse;
import com.detective.game.steam.dto.SteamPlayerSummary;
import com.detective.game.steam.dto.TokenDTO;
import com.detective.game.steam.jwt.JwtTokenProvider;
import com.detective.game.steam.jwt.UserDetailsImpl;
import com.detective.game.steam.service.RefreshTokenService;
import com.detective.game.steam.service.SteamApiClient;
import com.detective.game.steam.service.SteamOpenIdValidator;
import com.detective.game.steam.service.SteamUserService;
import com.detective.game.user.domain.PersonaState;
import com.detective.game.user.domain.ProfileVisibility;
import com.detective.game.user.domain.Role;
import com.detective.game.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/auth/steam")
@RequiredArgsConstructor
@Tag(name = "Steam Authentication", description = "Steam OpenID 인증 API")

public class SteamAuthController {
    private final SteamOpenIdValidator openIdValidator;
    private final SteamApiClient steamApiClient;
    private final SteamUserService userService;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Value("${steam.openid.realm}")
    private String openIdRealm;

    @Value("${steam.openid.return-url}")
    private String openIdReturnUrl;

    @Value("${game.client.success-redirect}")
    private String successRedirect;

    @Value("${game.client.failure-redirect}")
    private String failureRedirect;

    /**
     * Steam 로그인 시작
     * 브라우저를 Steam OpenID 페이지로 리다이렉트
     *
     * GET /api/auth/steam/login
     */
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

    /**
     * Steam 로그인 콜백
     * Steam 로그인 완료 후 돌아오는 엔드포인트
     * OpenID 검증 → Steam API 조회 → DB 저장 → JWT 발급
     *
     * GET /api/auth/steam/callback?openid.xxx=yyy
     */
    @GetMapping("/callback")
    @Operation(summary = "Steam 로그인 콜백",
            description = "Steam 로그인 후 콜백을 처리하고 JWT를 발급합니다.")
    public void callback(@RequestParam Map<String, String> params,
                         HttpServletResponse response) throws IOException {

        log.info("Steam 로그인 콜백 수신: paramCount={}", params.size());
        log.debug("콜백 파라미터: {}", params.keySet());

        try {
            // 1. OpenID 검증 및 Steam ID 추출
            String steamId = openIdValidator.validateAndExtractSteamId(params);
            log.info("Steam ID 추출 성공: steamId={}", steamId);

            // 2. Steam API로 사용자 정보 조회
            SteamPlayerSummary summary = steamApiClient.getPlayerSummary(steamId);
            log.info("Steam 사용자 정보 조회 성공: personaName={}", summary.getPersonaName());

            // 3. DB에 사용자 저장/업데이트
            User user = userService.findBySteamId(steamId)
                    .map(existingUser -> updateUser(existingUser, summary))
                    .orElseGet(() -> createUser(summary));

            log.info("사용자 DB 처리 완료: userId={}, steamId={}", user.getId(), user.getSteamId());

            // 4. JWT 생성
            TokenDTO tokens = tokenProvider.generateTokens(
                    user.getSteamId(),
                    user.getId(),
                    user.getRole().name()
            );

            log.info("JWT 발급 완료: userId={}", user.getId());

            // 5. Refresh Token DB 저장
            refreshTokenService.saveRefreshToken(tokens.getRefreshToken(), user.getId());

            // 6. 게임 클라이언트로 리다이렉트 (Deep Link)
            String redirectUrl = buildSuccessRedirectUrl(tokens, user.getId());

            log.info("게임 클라이언트로 리다이렉트: userId={}", user.getId());
            response.sendRedirect(redirectUrl);

        } catch (AuthException e) {
            log.error("인증 실패: {}", e.getMessage());
            handleAuthError(response, e.getMessage());

        } catch (Exception e) {
            log.error("Steam 로그인 처리 중 예외 발생: {}", e.getMessage(), e);
            handleAuthError(response, "로그인 처리 중 오류가 발생했습니다.");
        }
    }

    /**
     * 로그인 상태 확인 (테스트용)
     * Authorization 헤더로 JWT 전달 시 사용자 정보 반환
     *
     * GET /api/auth/steam/me
     */
    @GetMapping("/me")
    @Operation(
            summary = "현재 로그인 사용자 조회",
            description = "JWT 토큰으로 현재 로그인한 사용자 정보를 조회합니다."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<User>> getCurrentUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        log.info("현재 사용자 조회: userId={}", userDetails.getId());

        User user = userService.findBySteamId(userDetails.getSteamId())
                .orElseThrow(() -> new AuthException(ErrorMessage.USER_NOT_FOUND));

        return ResponseEntity.ok(ApiResponse.success("사용자 조회 성공", user));
    }

    // ========================================
    // Private Helper Methods
    // ========================================

    /**
     * 신규 사용자 생성
     */
    private User createUser(SteamPlayerSummary summary) {
        User newUser = User.builder()
                .steamId(summary.getSteamId())
                .username(summary.getPersonaName())
                .profileUrl(summary.getProfileUrl())
                .avatarUrl(summary.getAvatarMedium())
                .avatarFullUrl(summary.getAvatarFull())
                .realName(summary.getRealName())
                .countryCode(summary.getLocCountryCode())
                .role(Role.USER)
                .isActive(true)
                .profileVisibility(ProfileVisibility.fromCode(summary.getCommunityVisibilityState()))
                .personaState(PersonaState.fromCode(summary.getPersonaState()))
                .timeCreated(convertToLocalDateTime(summary.getTimeCreated()))
                .lastLogin(LocalDateTime.now())
                .loginCount(1)
                .build();

        User savedUser = userService.save(newUser);
        log.info("신규 사용자 생성 완료: userId={}, steamId={}, username={}",
                savedUser.getId(), savedUser.getSteamId(), savedUser.getUsername());

        return savedUser;
    }

    /**
     * 기존 사용자 정보 업데이트
     */
    private User updateUser(User user, SteamPlayerSummary summary) {
        // Builder로 새 객체 생성 (불변성 유지)
        User updatedUser = User.builder()
                .id(user.getId())
                .steamId(user.getSteamId())
                .username(summary.getPersonaName())
                .profileUrl(summary.getProfileUrl())
                .avatarUrl(summary.getAvatarMedium())
                .avatarFullUrl(summary.getAvatarFull())
                .realName(summary.getRealName())
                .countryCode(summary.getLocCountryCode())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .profileVisibility(ProfileVisibility.fromCode(summary.getCommunityVisibilityState()))
                .personaState(PersonaState.fromCode(summary.getPersonaState()))
                .timeCreated(user.getTimeCreated())
                .lastLogin(LocalDateTime.now())
                .loginCount(user.getLoginCount() + 1)
                .build();

        User savedUser = userService.save(updatedUser);
        log.info("사용자 정보 업데이트 완료: userId={}, loginCount={}",
                savedUser.getId(), savedUser.getLoginCount());

        return savedUser;
    }

    /**
     * Unix 타임스탬프 → LocalDateTime 변환
     */
    private LocalDateTime convertToLocalDateTime(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(timestamp),
                ZoneId.systemDefault()
        );
    }

    /**
     * 성공 리다이렉트 URL 생성
     */
    private String buildSuccessRedirectUrl(TokenDTO tokens, Long userId) {
        return String.format(
                "%s?accessToken=%s&refreshToken=%s&userId=%d",
                successRedirect,
                URLEncoder.encode(tokens.getAccessToken(), StandardCharsets.UTF_8),
                URLEncoder.encode(tokens.getRefreshToken(), StandardCharsets.UTF_8),
                userId
        );
    }

    /**
     * 에러 처리
     */
    private void handleAuthError(HttpServletResponse response, String errorMessage) throws IOException {
        String redirectUrl = String.format(
                "%s?error=%s",
                failureRedirect,
                URLEncoder.encode(errorMessage, StandardCharsets.UTF_8)
        );
        response.sendRedirect(redirectUrl);
    }
}