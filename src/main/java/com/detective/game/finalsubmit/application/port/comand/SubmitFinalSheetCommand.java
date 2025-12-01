package com.detective.game.finalsubmit.application.port.comand;

import java.util.List;

public record SubmitFinalSheetCommand(
        String roomId,
        boolean isFinal,
        List<String> answers
) {
    public static SubmitFinalSheetCommand of(String roomId, boolean isFinal, List<String> answers) {
        return new SubmitFinalSheetCommand(roomId, isFinal, answers);
    }
}
