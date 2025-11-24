package com.detective.game.ai.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AIChatMessage {
    private final String role;     // "user" or "assistant"
    private final String content;

    public static AIChatMessage of(String role, String content) {
        return new AIChatMessage(role, content);
    }
}
