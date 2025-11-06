package com.detective.game.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    //공통 에러
    INVALID_REQUEST(BAD_REQUEST, "잘못된 요청입니다"),
    INVALID_PARAMETER(BAD_REQUEST, "잘못된 파라미터입니다."),
    RESOURCE_NOT_FOUND(NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "예상치 못한 오류가 발생했습니다."),

    // JWT 관련 에러
    JWT_INVALID_SIGNATURE(UNAUTHORIZED, "JWT 서명이 유효하지 않습니다."),
    JWT_MALFORMED(UNAUTHORIZED, "잘못된 형식의 JWT입니다."),
    JWT_UNSUPPORTED(UNAUTHORIZED, "지원하지 않는 JWT입니다."),
    JWT_CLAIMS_EMPTY(UNAUTHORIZED, "JWT claims가 비어있습니다."),
    JWT_EXPIRED(UNAUTHORIZED, "JWT 토큰이 만료되었습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
