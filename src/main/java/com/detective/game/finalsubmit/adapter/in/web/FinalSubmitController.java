package com.detective.game.finalsubmit.adapter.in.web;

import com.detective.game.finalsubmit.adapter.in.web.dto.FinalSubmitRequest;
import com.detective.game.finalsubmit.application.port.comand.SubmitFinalSheetCommand;
import com.detective.game.finalsubmit.application.port.in.SubmitFinalSheetUseCase;
import com.detective.game.roomcontext.application.port.command.SaveDoorStateCommand;
import com.detective.game.roomcontext.application.port.command.SaveNoteItemCommand;
import com.detective.game.roomcontext.application.port.in.SaveDoorStateUseCase;
import com.detective.game.roomcontext.application.port.in.SaveNoteItemUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FinalSubmitController {

    private final SubmitFinalSheetUseCase submitFinalSheetUseCase;
    private final SaveNoteItemUseCase saveNoteItemUseCase;
    private final SaveDoorStateUseCase saveDoorStateUseCase;

    @PostMapping("/final-submit")
    public void submit(
            @RequestHeader("Room-Id") String roomId,
            @RequestBody FinalSubmitRequest req
    ) {
        // 1) sharedInventory / doorStates도 RoomAIContext에 반영 (혹시 누락된 이벤트가 있을 수 있으니까)
        if (req.getSharedInventory() != null) {
            req.getSharedInventory().forEach(it -> {
                SaveNoteItemCommand noteCommand = new SaveNoteItemCommand(
                        roomId,
                        it.getNoteName(),
                        it.isHeSpy(),
                        it.getItemId()
                );
                saveNoteItemUseCase.save(noteCommand);
            });
        }
        if (req.getDoorStates() != null) {
            req.getDoorStates().forEach(ds -> {
                SaveDoorStateCommand doorCommand = new SaveDoorStateCommand(
                        roomId,
                        ds.getDoorId(),
                        ds.isLocked()
                );
                saveDoorStateUseCase.save(doorCommand);
            });
        }

        SubmitFinalSheetCommand submitCommand = new SubmitFinalSheetCommand(
                roomId,
                req.getSheet().isFinal(),
                req.getSheet().getAnswers()
        );

        submitFinalSheetUseCase.submit(submitCommand);
    }
}