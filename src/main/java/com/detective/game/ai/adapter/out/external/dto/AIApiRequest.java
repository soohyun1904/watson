package com.detective.game.ai.adapter.out.external.dto;

import com.detective.game.ai.application.port.command.AskAIOutboundCommand;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AIApiRequest {

    private String question;

    @JsonProperty("acquired_clue_list")
    private List<String> acquiredClueList;

    @JsonProperty("chat_history")
    private List<String> chatHistory;

    public static AIApiRequest from(AskAIOutboundCommand command) {
        return new AIApiRequest(
                command.getQuestion(),
                command.getClueIds(),  // 단서 ID 목록 (예: ["A1", "C1"])
                command.getChatHistory()  // 채팅 히스토리
        );
    }
}
