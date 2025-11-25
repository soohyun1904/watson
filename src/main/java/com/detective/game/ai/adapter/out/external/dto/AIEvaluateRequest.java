package com.detective.game.ai.adapter.out.external.dto;

import com.detective.game.ai.application.port.command.EvaluateFinalSubmitCommand;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AIEvaluateRequest {

    @JsonProperty("conclusion")
    private String conclusion;

    @JsonProperty("selected_evidence_ids")
    private List<String> selectedEvidenceIds;

    @JsonProperty("total_collected_ids")
    private List<String> totalCollectedIds;

    @JsonProperty("reasoning_text")
    private String reasoningText;

    public static AIEvaluateRequest from(EvaluateFinalSubmitCommand command) {

        List<String> answers = command.getAnswers();

        String conclusionRaw = answers.size() > 0 ? answers.get(0) : "";
        String conclusion = "밀정".equals(conclusionRaw) ? "miljeong" : "anti_miljeong";

        List<String> selectedIds = answers.size() > 1
                ? Arrays.stream(answers.get(1).split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList()
                : List.of();

        String reasoning = answers.size() > 2 ? answers.get(2) : "";

        List<String> totalCollected = command.getClueContents();

        return new AIEvaluateRequest(
                conclusion,
                selectedIds,
                totalCollected,
                reasoning
        );
    }
}