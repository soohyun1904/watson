package com.detective.game.ai.adapter.out.external.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AIEvaluateResponse {

    @JsonProperty("total_score")
    private Integer score;

    @JsonProperty("grade")
    private String grade;

    @JsonProperty("feedback")
    private String feedback;
}