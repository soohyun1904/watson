package com.detective.game.ai.adapter.in.web;

import com.detective.game.ai.adapter.in.web.dto.AskAIRequest;
import com.detective.game.ai.adapter.in.web.dto.AskAIResponse;
import com.detective.game.ai.application.port.in.AskAIUseCase;
import com.detective.game.ai.domain.AIAnswer;
import com.detective.game.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class AIController {

    private final AskAIUseCase askAIUseCase;

    @PostMapping("/{roomId}/ai/ask")
    public ApiResponse<AskAIResponse> ask(
            @PathVariable String roomId,
            @RequestBody AskAIRequest request
    ) {
        AIAnswer answer = askAIUseCase.ask(roomId, request.getQuestion());
        AskAIResponse dto = new AskAIResponse(answer.getText());
        return ApiResponse.success(dto);
    }
}
