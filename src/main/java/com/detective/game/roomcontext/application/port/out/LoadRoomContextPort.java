package com.detective.game.roomcontext.application.port.out;

import com.detective.game.roomcontext.domain.RoomAIContext;

public interface LoadRoomContextPort {
    RoomAIContext load(String roomId);
}