package com.detective.game.user.domain;

import com.detective.game.common.exception.SteamException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.detective.game.common.exception.ErrorMessage.STEAM_INVALID_VISIBILITY_CODE;

@Getter
@RequiredArgsConstructor
public enum ProfileVisibility {
    PRIVATE(1, "비공개"),
    PUBLIC(3, "공개");

    private final int code;
    private final String description;

    //Steam API에서 받은 숫자 → Enum 변환
    public static ProfileVisibility fromCode(int code) {
        return Arrays.stream(values())
                .filter(v -> v.code==code)
                .findFirst()
                .orElseThrow(() -> new SteamException(STEAM_INVALID_VISIBILITY_CODE));
    }
}
