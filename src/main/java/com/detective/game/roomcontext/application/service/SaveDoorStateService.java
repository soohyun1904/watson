package com.detective.game.roomcontext.application.service;

import com.detective.game.roomcontext.application.port.command.SaveDoorStateCommand;
import com.detective.game.roomcontext.application.port.in.SaveDoorStateUseCase;
import com.detective.game.roomcontext.application.port.out.LoadRoomContextPort;
import com.detective.game.roomcontext.application.port.out.SaveRoomContextPort;
import com.detective.game.roomcontext.domain.DoorState;
import com.detective.game.roomcontext.domain.RoomAIContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaveDoorStateService implements SaveDoorStateUseCase {

    private final LoadRoomContextPort loadPort;
    private final SaveRoomContextPort savePort;

    @Override
    public void save(SaveDoorStateCommand command) {
        RoomAIContext ctx = loadPort.load(command.roomId());
        DoorState state = DoorState.of(command.doorId(), command.locked());
        ctx.updateDoorState(state);
        savePort.save(ctx);
    }
}
