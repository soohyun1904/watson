package com.detective.game.roomcontext.application.port.command;

public record SaveNoteItemCommand(
        String roomId,
        String noteName,
        boolean heSpy,
        String itemId
) {
    public static SaveNoteItemCommand of(String roomId,
                                         String noteName,
                                         boolean heSpy,
                                         String itemId){
        return new SaveNoteItemCommand(roomId, noteName, heSpy, itemId);
    }
}