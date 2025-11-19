package com.detective.game.auth.domain.exception;

import com.detective.game.common.exception.BaseException;
import com.detective.game.common.exception.ErrorMessage;

public class AuthDomainException extends BaseException {
    public AuthDomainException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
