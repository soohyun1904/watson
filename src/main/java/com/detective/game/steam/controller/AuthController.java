package com.detective.game.steam.controller;

import com.detective.game.common.response.ApiResponse;
import com.detective.game.steam.dto.AccessTokenDTO;
import com.detective.game.steam.dto.RefreshTokenDTO;
import com.detective.game.steam.jwt.UserDetailsImpl;
import com.detective.game.steam.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 토큰 관리 API
 * (Steam 인증은 SteamAuthController에서 처리)
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Token Management", description = "토큰 갱신 및 로그아웃 API")
public class AuthController {
    private final AuthService authService;

    /**
     * Access Token 갱신
     * Refresh Token으로 새로운 Access Token 발급
     *
     * POST /api/auth/refresh
     * Body: { "refreshToken": "xxx" }
     */
    @PostMapping("/refresh")
    @Operation(
            summary = "토큰 갱신",
            description = "Refresh Token으로 새로운 Access Token을 발급합니다."
    )
    public ResponseEntity<ApiResponse<AccessTokenDTO>> refreshToken(
            @RequestBody RefreshTokenDTO refreshTokenDTO) {

        log.info("토큰 갱신 요청");
        AccessTokenDTO newAccessToken = authService.refreshToken(refreshTokenDTO);

        return ResponseEntity.ok(
                ApiResponse.success("토큰 갱신이 완료되었습니다.", newAccessToken)
        );
    }

    /**
     * 로그아웃
     * Refresh Token을 DB에서 삭제
     *
     * POST /api/auth/logout
     * Header: Authorization: Bearer {accessToken}
     */
    @PostMapping("/logout")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "로그아웃",
            description = "Refresh Token을 삭제하여 로그아웃합니다. Authorization 헤더에 Access Token이 필요합니다."
    )
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = userDetails.getId();
        log.info("로그아웃 요청: userId={}", userId);

        authService.logout(userId);

        return ResponseEntity.ok(
                ApiResponse.success("로그아웃되었습니다.", null)
        );
    }
}
