package com.detective.game.common.exception;

/**
 * Steam 관련 예외
 */
public class SteamException extends BaseException{
    public SteamException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
