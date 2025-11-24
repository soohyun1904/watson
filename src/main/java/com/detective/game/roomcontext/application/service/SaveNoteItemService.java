package com.detective.game.roomcontext.application.service;

import com.detective.game.roomcontext.application.port.command.SaveNoteItemCommand;
import com.detective.game.roomcontext.application.port.in.SaveNoteItemUseCase;
import com.detective.game.roomcontext.application.port.out.LoadRoomContextPort;
import com.detective.game.roomcontext.application.port.out.SaveRoomContextPort;
import com.detective.game.roomcontext.domain.NoteItem;
import com.detective.game.roomcontext.domain.RoomAIContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaveNoteItemService implements SaveNoteItemUseCase {

    private final LoadRoomContextPort loadPort;
    private final SaveRoomContextPort savePort;

    @Override
    public void save(SaveNoteItemCommand command) {
        RoomAIContext ctx = loadPort.load(command.roomId());
        NoteItem item = NoteItem.of(command.noteName(), command.heSpy(), command.itemId());
        
        System.out.println("=== DEBUG SAVE NOTE ITEM ===");
        System.out.println("Room ID: " + command.roomId());
        System.out.println("Note Name: " + command.noteName());
        System.out.println("Before Add - Inventory Size: " + ctx.getSharedInventory().size());
        
        ctx.addNoteItem(item);
        
        System.out.println("After Add - Inventory Size: " + ctx.getSharedInventory().size());
        System.out.println("Clue Codes: " + ctx.getCollectedClueCodes());
        
        savePort.save(ctx);
    }
}
