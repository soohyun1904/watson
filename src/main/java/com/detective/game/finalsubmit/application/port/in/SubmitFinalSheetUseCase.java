package com.detective.game.finalsubmit.application.port.in;

import com.detective.game.finalsubmit.application.port.comand.SubmitFinalSheetCommand;

public interface SubmitFinalSheetUseCase {
    void submit(SubmitFinalSheetCommand command);
}
