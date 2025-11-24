package com.detective.game.roomcontext.application.port.command;

public record SaveDoorStateCommand(
        String roomId,
        String doorId,
        boolean locked
) {
    public static SaveDoorStateCommand of(String roomId, String doorId, boolean locked){
        return new SaveDoorStateCommand(roomId, doorId, locked);
    }
}
