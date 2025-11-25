package com.detective.game.finalsubmit.application.port.comand;

import java.util.List;

public record SubmitFinalSheetCommand(
        String roomId,
        boolean isFinal,
        List<String> answers
) { }
