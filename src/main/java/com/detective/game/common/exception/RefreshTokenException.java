package com.detective.game.common.exception;

/**
 * 리프레쉬 토큰 관련 예외
 */
public class RefreshTokenException extends BaseException {
    public RefreshTokenException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
