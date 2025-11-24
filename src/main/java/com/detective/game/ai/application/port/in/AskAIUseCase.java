package com.detective.game.ai.application.port.in;

import com.detective.game.ai.domain.AIAnswer;

public interface AskAIUseCase {
    AIAnswer ask(String roomId, String question);
}
