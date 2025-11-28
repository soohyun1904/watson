package com.detective.game.roomcontext.adapter.in.web;

import com.detective.game.common.response.ApiResponse;
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
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<Void>> init(@RequestHeader("Room-Id") String roomId) {
        RoomAIContext ctx = new RoomAIContext(roomId);
        saveRoomContextPort.save(ctx);
        return ResponseEntity.ok(
                ApiResponse.success("방을 생성하였습니다.", null)
        );
    }

    /**
     * 강제 종료 API (언리얼 세션 강제 종료 시)
     */
    @DeleteMapping("/force")
    public ResponseEntity<ApiResponse<Void>> forceDelete(@RequestHeader("Room-Id") String roomId) {
        deleteRoomContextPort.delete(roomId);
        return ResponseEntity.ok(
                ApiResponse.success("방을 강제 종료합니다.", null)
        );
    }
    /**
     * 증거/노트 수집 이벤트
     */
    @PostMapping("/noteItem")
    public ResponseEntity<ApiResponse<Void>> noteItem(
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

        return ResponseEntity.ok(
                ApiResponse.success("아이템을 수집하였습니다.", null)
        );
    }

    /**
     * 문 잠김/해금 이벤트
     */
    @PostMapping("/doorState")
    public ResponseEntity<ApiResponse<Void>>  doorState(
            @RequestHeader("Room-Id") String roomId,
            @RequestBody DoorStateRequest request
    ) {
        SaveDoorStateCommand command = SaveDoorStateCommand.of(roomId, request.getDoorId(), request.isLocked());
        saveDoorStateUseCase.save(command);

        return ResponseEntity.ok(
                ApiResponse.success("문을 해금하였습니다.", null)
        );
    }
}
