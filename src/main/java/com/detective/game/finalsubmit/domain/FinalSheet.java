package com.detective.game.finalsubmit.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FinalSheet {
    private final boolean finalSubmit;  // isFinal
    private final List<String> answers; // ["Q1 Answer", ...]

    public static FinalSheet of(boolean finalSubmit, List<String> answers) {
        return new FinalSheet(finalSubmit, answers);
    }
}
