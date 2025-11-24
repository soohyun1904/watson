package com.detective.game.clue.adapter.out.persistence;

import com.detective.game.clue.application.port.out.LoadCluePort;
import com.detective.game.clue.domain.Clue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CluePersistenceAdapter implements LoadCluePort {

    private final ClueJpaRepository repository;

    @Override
    public List<Clue> loadByCodes(List<String> codes) {
        return repository.findByCodeIn(codes).stream()
                .map(entity -> Clue.of(
                        entity.getId(),
                        entity.getCode(),
                        entity.getCategory(),
                        entity.getTitle(),
                        entity.getSummary(),
                        entity.getContent()
                ))
                .toList();
    }
}