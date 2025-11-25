package com.detective.game.ai.application.port.out;

import com.detective.game.ai.application.port.command.AskAIOutboundCommand;
import com.detective.game.ai.application.port.out.dto.AIApiRawAnswer;

public interface CallAIPort {
    AIApiRawAnswer call(AskAIOutboundCommand command);
}
