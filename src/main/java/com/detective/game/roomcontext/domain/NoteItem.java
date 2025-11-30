package com.detective.game.roomcontext.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NoteItem {
    private final String noteName;
    private final boolean heSpy;    // isHeSpy
    private final String itemId;    //"A1", "B3"

    public static NoteItem of(String noteName, boolean heSpy, String itemId) {
        return new NoteItem(noteName, heSpy, itemId);
    }
}
