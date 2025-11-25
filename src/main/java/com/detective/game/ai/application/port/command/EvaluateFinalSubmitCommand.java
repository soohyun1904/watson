package com.detective.game.ai.application.port.command;

import com.detective.game.clue.domain.Clue;
import com.detective.game.finalsubmit.domain.FinalSheet;
import com.detective.game.roomcontext.domain.RoomAIContext;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class EvaluateFinalSubmitCommand {

    private final String roomId;
    private final List<String> answers;
    private final List<String> clueContents;
    private final List<InventoryItem> inventory;
    private final List<DoorInfo> doorStates;

    @Getter
    @AllArgsConstructor
    public static class InventoryItem {
        private final String noteName;
        private final boolean heSpy;
        private final String itemId;
    }

    @Getter
    @AllArgsConstructor
    public static class DoorInfo {
        private final String doorId;
        private final boolean locked;
    }

    public static EvaluateFinalSubmitCommand from(
            String roomId,
            FinalSheet sheet,
            List<Clue> clues,
            RoomAIContext ctx
    ) {
        return new EvaluateFinalSubmitCommand(
                roomId,
                sheet.getAnswers(),
                clues.stream()
                        .map(Clue::getContent)
                        .toList(),
                ctx.getSharedInventory().stream()
                        .map(i -> new InventoryItem(i.getNoteName(), i.isHeSpy(), i.getItemId()))
                        .toList(),
                ctx.getDoorStateValues().stream()
                        .map(d -> new DoorInfo(d.getDoorId(), d.isLocked()))
                        .toList()
        );
    }
}
