package com.detective.game.roomcontext.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DoorState {
    private final String doorId;
    private final boolean locked;

    public static DoorState of(String doorId, boolean locked) {
        return new DoorState(doorId, locked);
    }
}
