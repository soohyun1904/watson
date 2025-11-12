package com.detective.game.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum PersonaState {

    OFFLINE(0, "오프라인"),
    ONLINE(1, "온라인"),
    BUSY(2, "다른 용무 중"),
    AWAY(3, "자리 비움"),
    SNOOZE(4, "잠수 모드"),
    LOOKING_TO_TRADE(5, "거래 중"),
    LOOKING_TO_PLAY(6, "플레이 중");

    private final int code;
    private final String description;

    public static PersonaState fromCode(int code) {
        return Arrays.stream(values())
                .filter(v -> v.code == code)
                .findFirst()
                .orElse(OFFLINE);
    }
}
