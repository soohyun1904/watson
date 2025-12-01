package com.detective.game.roomcontext.application.service;

import com.detective.game.roomcontext.application.port.command.SaveNoteItemCommand;
import com.detective.game.roomcontext.application.port.in.SaveNoteItemUseCase;
import com.detective.game.roomcontext.application.port.out.LoadRoomContextPort;
import com.detective.game.roomcontext.application.port.out.SaveRoomContextPort;
import com.detective.game.roomcontext.domain.NoteItem;
import com.detective.game.roomcontext.domain.RoomAIContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaveNoteItemService implements SaveNoteItemUseCase {

    private final LoadRoomContextPort loadPort;
    private final SaveRoomContextPort savePort;

    @Override
    public void save(SaveNoteItemCommand command) {
        RoomAIContext ctx = loadPort.load(command.roomId());
        NoteItem item = NoteItem.of(command.noteName(), command.heSpy(), command.itemId());
        
        log.info("=== DEBUG SAVE NOTE ITEM ===");
        log.info("Room ID: " + command.roomId());
        log.info("Note Name: " + command.noteName());
        log.info("Before Add - Inventory Size: " + ctx.getSharedInventory().size());
        
        ctx.addNoteItem(item);

        log.info("After Add - Inventory Size: " + ctx.getSharedInventory().size());
        log.info("Clue Codes: " + ctx.getCollectedClueCodes());
        
        savePort.save(ctx);
    }
}
