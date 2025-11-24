package com.detective.game.ai.adapter.out.external.dto;

import com.detective.game.ai.application.port.command.EvaluateFinalSubmitCommand;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AIEvaluateRequest {

    private String roomId;
    private List<String> answers;
    private List<String> contextClues;
    private List<InventoryItemDto> inventory;
    private List<DoorStateDto> doorStates;

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class InventoryItemDto {
        private final String noteName;
        private final boolean heSpy;
        private final String itemId;
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class DoorStateDto {
        private final String doorId;
        private final boolean locked;
    }

    public static AIEvaluateRequest from(EvaluateFinalSubmitCommand command) {

        List<InventoryItemDto> inv = command.getInventory().stream()
                .map(i -> new InventoryItemDto(i.getNoteName(), i.isHeSpy(), i.getItemId()))
                .toList();

        List<DoorStateDto> ds = command.getDoorStates().stream()
                .map(d -> new DoorStateDto(d.getDoorId(), d.isLocked()))
                .toList();

        return new AIEvaluateRequest(
                command.getRoomId(),
                command.getAnswers(),
                command.getClueContents(),
                inv,
                ds
        );
    }
}
