package com.detective.game.ai.application.port.out;

import com.detective.game.ai.application.port.command.EvaluateFinalSubmitCommand;
import com.detective.game.ai.application.port.out.dto.AIEvaluateRawResponse;

public interface EvaluateFinalSubmitPort {
    AIEvaluateRawResponse evaluate(EvaluateFinalSubmitCommand command);
}
