package com.detective.game.roomcontext.application.port.in;

import com.detective.game.roomcontext.application.port.command.SaveDoorStateCommand;
import com.detective.game.roomcontext.domain.DoorState;

public interface SaveDoorStateUseCase {
    void save(SaveDoorStateCommand command);
}
