package com.detective.game.roomcontext.adapter.in.web;

import com.detective.game.roomcontext.adapter.in.web.dto.DoorStateRequest;
import com.detective.game.roomcontext.adapter.in.web.dto.NoteItemRequest;
import com.detective.game.roomcontext.application.port.command.SaveDoorStateCommand;
import com.detective.game.roomcontext.application.port.command.SaveNoteItemCommand;
import com.detective.game.roomcontext.application.port.in.SaveDoorStateUseCase;
import com.detective.game.roomcontext.application.port.in.SaveNoteItemUseCase;
import com.detective.game.roomcontext.application.port.out.DeleteRoomContextPort;
import com.detective.game.roomcontext.application.port.out.SaveRoomContextPort;
import com.detective.game.roomcontext.domain.RoomAIContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/room-context")
@RequiredArgsConstructor
public class RoomContextController {

    private final SaveNoteItemUseCase saveNoteItemUseCase;
    private final SaveDoorStateUseCase saveDoorStateUseCase;
    private final SaveRoomContextPort saveRoomContextPort;
    private final DeleteRoomContextPort deleteRoomContextPort;

    /**
     * 방 초기화 (언리얼이 세션 만든 직후 호출)
     */
    @PostMapping("/init")
    public void init(@RequestHeader("Room-Id") String roomId) {
        RoomAIContext ctx = new RoomAIContext(roomId);
        saveRoomContextPort.save(ctx);
    }

    /**
     * 강제 종료 API (언리얼 세션 강제 종료 시)
     */
    @DeleteMapping("/force")
    public void forceDelete(@RequestHeader("Room-Id") String roomId) {
        deleteRoomContextPort.delete(roomId);
    }
    /**
     * 증거/노트 수집 이벤트
     */
    @PostMapping("/noteItem")
    public void noteItem(
            @RequestHeader("Room-Id") String roomId,
            @RequestBody NoteItemRequest request
    ) {
        // 여기서 noteName을 곧바로 clue 코드로 사용한다고 가정 (예: "A1")
        SaveNoteItemCommand command = SaveNoteItemCommand.of(
                roomId,
                request.getNoteName(),
                request.isHeSpy(),
                request.getItemId()
        );
        saveNoteItemUseCase.save(command);
    }

    /**
     * 문 잠김/해금 이벤트
     */
    @PostMapping("/doorState")
    public void doorState(
            @RequestHeader("Room-Id") String roomId,
            @RequestBody DoorStateRequest request
    ) {
        SaveDoorStateCommand command = SaveDoorStateCommand.of(roomId, request.getDoorId(), request.isLocked());
        saveDoorStateUseCase.save(command);
    }
}
