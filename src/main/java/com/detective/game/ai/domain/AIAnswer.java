package com.detective.game.ai.domain;

import com.detective.game.ai.application.port.out.dto.AIApiRawAnswer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AIAnswer {
    private final String text;

    public static AIAnswer of(String text){
        return new AIAnswer(text);
    }
}