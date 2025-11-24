package com.detective.game.auth.adapter.in.web;

import com.detective.game.auth.application.command.LogoutCommand;
import com.detective.game.auth.application.command.RefreshTokenCommand;
import com.detective.game.auth.application.port.in.AccessTokenResult;
import com.detective.game.auth.application.port.in.LogoutUseCase;
import com.detective.game.auth.application.port.in.RefreshTokenUseCase;
import com.detective.game.common.response.ApiResponse;
import com.detective.game.auth.adapter.in.web.dto.AccessTokenDTO;
import com.detective.game.auth.adapter.in.web.dto.RefreshTokenDTO;
import com.detective.game.auth.adapter.out.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Token Management", description = "토큰 갱신 및 로그아웃 API")
@RequiredArgsConstructor
public class AuthController {
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;

    @PostMapping("/refresh")
    @Operation(
            summary = "토큰 갱신",
            description = "Refresh Token으로 새로운 Access Token을 발급합니다."
    )
    public ResponseEntity<ApiResponse<AccessTokenDTO>> refresh(
            @RequestBody RefreshTokenDTO dto) {

        AccessTokenResult result = refreshTokenUseCase.refresh(
                new RefreshTokenCommand(dto.getRefreshToken())
        );

        AccessTokenDTO responseDto = new AccessTokenDTO(result.getAccessToken());
        return ResponseEntity.ok(
                ApiResponse.success("토큰 갱신이 완료되었습니다.", responseDto)
        );
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "로그아웃",
            description = "Refresh Token을 삭제하여 로그아웃합니다. Authorization 헤더에 Access Token이 필요합니다."
    )
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        logoutUseCase.logout(new LogoutCommand(userDetails.getId()));
        return ResponseEntity.ok(
                ApiResponse.success("로그아웃되었습니다.", null)
        );
    }
}
