package com.detective.game.ai.application.port.out.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AIEvaluateRawResponse {
    private final Integer score;
    private final String grade;
    private final String feedback;
}
