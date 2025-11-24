package com.detective.game.ai.adapter.out.external.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AIEvaluateResponse {
    private Integer score;
    private String feedback;
}
