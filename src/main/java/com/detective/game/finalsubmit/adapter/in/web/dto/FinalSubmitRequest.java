package com.detective.game.finalsubmit.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class FinalSubmitRequest {

    private SheetDto sheet;
    private List<NoteItemDto> sharedInventory;
    private List<DoorStateDto> doorStates;

    @Getter
    public static class SheetDto {

        @JsonProperty("is_final")
        private boolean isFinal;
        private List<String> answers;
    }

    @Getter
    public static class NoteItemDto {
        private String noteName;
        private boolean isHeSpy;
        private String itemId;
    }

    @Getter
    public static class DoorStateDto {
        private boolean isLocked;
        private String doorId;
    }
}
