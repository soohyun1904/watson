package com.detective.game.ai.application.service;

import com.detective.game.ai.application.port.command.AskAIOutboundCommand;
import com.detective.game.ai.application.port.in.AskAIUseCase;
import com.detective.game.ai.application.port.out.CallAIPort;
import com.detective.game.ai.application.port.out.dto.AIApiRawAnswer;
import com.detective.game.ai.domain.AIAnswer;
import com.detective.game.ai.domain.AIChatMessage;
import com.detective.game.clue.application.port.out.LoadCluePort;
import com.detective.game.clue.domain.Clue;
import com.detective.game.roomcontext.application.port.out.LoadRoomContextPort;
import com.detective.game.roomcontext.application.port.out.SaveRoomContextPort;
import com.detective.game.roomcontext.domain.RoomAIContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AskAIService implements AskAIUseCase {

    private final LoadRoomContextPort loadRoomContextPort;
    private final LoadCluePort loadCluePort;
    private final CallAIPort callAIPort;
    private final SaveRoomContextPort savePort;


    @Override
    public AIAnswer ask(String roomId, String question) {

        // 1) Room 컨텍스트에서 단서 코드 & 채팅 히스토리 가져오기
        RoomAIContext ctx = loadRoomContextPort.load(roomId);
        List<String> clueCodes = ctx.getCollectedClueCodes();
        List<AIChatMessage> chatHistory = ctx.getChatHistory();
        
        System.out.println("=== DEBUG AI SERVICE ===");
        System.out.println("Room ID: " + roomId);
        System.out.println("Collected Clue Codes: " + clueCodes);
        System.out.println("Inventory Size: " + ctx.getSharedInventory().size());

        // 2) 코드로 Clue 전체 조회
        List<Clue> clues = clueCodes.isEmpty()
                ? List.of()
                : loadCluePort.loadByCodes(clueCodes);
                
        System.out.println("Found Clues from DB: " + clues.size());
        clues.forEach(c -> System.out.println("  - " + c.getCode() + ": " + c.getContent()));

        // 3) OutboundCommand 생성
        AskAIOutboundCommand command =
                AskAIOutboundCommand.from(question, clues, chatHistory);

        // 4) 외부 AI 호출
        AIApiRawAnswer raw = callAIPort.call(command);
        AIAnswer answer = AIAnswer.of(raw.getAnswer());

        // 5) 채팅 히스토리 저장
        ctx.addChatMessage(AIChatMessage.of("USER",answer.getText()));
        savePort.save(ctx);

        return answer;
    }
}
