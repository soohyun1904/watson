package com.detective.game.roomcontext.adapter.in.web.dto;

import lombok.Getter;

@Getter
public class DoorStateRequest {
    private boolean isLocked;
    private String doorId;
}
