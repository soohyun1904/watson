package com.detective.game.ai.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class AIResult {
    private final Integer score;
    private final String feedback;

    public static AIResult of(Integer score, String feedback) {
        return new AIResult(score, feedback);
    }
}
