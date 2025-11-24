package com.detective.game.roomcontext.application.port.in;

import com.detective.game.roomcontext.application.port.command.SaveNoteItemCommand;

public interface SaveNoteItemUseCase {
    void save(SaveNoteItemCommand command);
}
