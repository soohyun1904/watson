package com.detective.game.auth.application.port.out.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

@Value
@Schema(description = "인증 토큰 정보")
public class TokenPair {
    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    String accessToken;

    @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    String refreshToken;
}
