package com.detective.game.common.exception;

import com.detective.game.common.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException ex){
        ErrorMessage errorMessage = ex.getErrorMessage();
        logger.error("BaseException: {}", errorMessage.getMessage(), ex);
        return ResponseEntity.status(errorMessage.getHttpStatus())
                .body(ApiResponse.error(errorMessage.getHttpStatus().value(), errorMessage.getMessage()));
    }

}
