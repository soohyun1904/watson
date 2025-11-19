package com.detective.game.auth.application.port.in;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SteamLoginResult {
    private final Long userId;
    private final String accessToken;
    private final String refreshToken;
}
