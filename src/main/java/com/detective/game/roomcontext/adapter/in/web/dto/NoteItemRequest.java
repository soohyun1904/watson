package com.detective.game.roomcontext.adapter.in.web.dto;

import lombok.Getter;

@Getter
public class NoteItemRequest {
    private String noteName;
    private boolean isHeSpy;
    private String itemId;
}
