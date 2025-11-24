package com.detective.game.ai.application.port.command;

import com.detective.game.ai.domain.AIChatMessage;
import com.detective.game.clue.domain.Clue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AskAIOutboundCommand {
    private String question;
    private List<String> clueIds;   // 단서 내용 목록
    private List<String> chatHistory;    // 채팅 히스토리

    public static AskAIOutboundCommand from(
            String question,
            List<Clue> clues,
            List<AIChatMessage> chatMessages
    ) {
        return new AskAIOutboundCommand(
                question,
                clues.stream()
                        .map(Clue::getCode)
                        .toList(),
                chatMessages.stream()
                        .map(AIChatMessage::getContent)  // 채팅 내용 추출
                        .toList()
        );
    }
}
