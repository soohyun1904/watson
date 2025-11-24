package com.detective.game.clue.application.port.out;

import com.detective.game.clue.domain.Clue;

import java.util.List;

public interface LoadCluePort {

    List<Clue> loadByCodes(List<String> codes);
}
