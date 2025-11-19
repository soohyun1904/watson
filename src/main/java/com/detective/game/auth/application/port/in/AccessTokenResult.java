package com.detective.game.auth.application.port.in;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AccessTokenResult {
    private final String accessToken;
}
