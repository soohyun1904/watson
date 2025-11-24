package com.detective.game.clue.domain;

import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Clue {
    private final Long id;
    private final String code;      // A1, B3 ...
    private final String category;  // "A" or "B"
    private final String title;
    private final String summary;
    private final String content;

    public static Clue of(Long id, String code, String category, String title, String summary, String content) {
        return new Clue(id, code, category, title, summary, content);
    }
}
