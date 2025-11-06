package com.detective.game.common.exception;

/**
 * 인증 관련 예외
 */
public class AuthException extends BaseException{
    public AuthException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
