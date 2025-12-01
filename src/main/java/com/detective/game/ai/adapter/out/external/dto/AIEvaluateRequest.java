package com.detective.game.ai.adapter.out.external.dto;

import com.detective.game.ai.application.port.command.EvaluateFinalSubmitCommand;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
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

        boolean isMiljeong = command.isFinal();


        // 결론 코드 변환
        String conclusion = isMiljeong ? "miljeong" : "anti_miljeong";

        // 전체 수집 단서 ID
        List<String> totalCollected = command.getClueIds();

        // 근거 자동 추출 (A = 밀정, B = 비밀정)
        List<String> selectedIds = totalCollected.stream()
                .filter(id -> isMiljeong ? id.startsWith("A") : id.startsWith("B"))
                .toList();

        // 서술형 이유
        String reasoning = command.getAnswers().get(0);

        return new AIEvaluateRequest(
                conclusion,
                selectedIds,
                totalCollected,
                reasoning
        );
   }
}