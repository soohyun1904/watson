package com.detective.game.common.exception;

/**
 * 리프레쉬 토큰 관련 예외
 */
public class RefreshTokenException extends RuntimeException {
    public RefreshTokenException(String message) {
        super(message);
    }
}
