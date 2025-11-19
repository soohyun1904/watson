package com.detective.game.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    // 유저 관련 에러
    USER_NOT_FOUND(NOT_FOUND, "존재하지 않는 회원입니다."),
    USER_EMAIL_ALREADY_EXISTS(BAD_REQUEST, "이미 사용 중인 이메일입니다."),
    USER_USERNAME_ALREADY_EXISTS(BAD_REQUEST, "이미 사용 중인 사용자 이름입니다."),
    USER_INVALID_CREDENTIALS(UNAUTHORIZED, "잘못된 이메일 또는 비밀번호입니다."),
    USER_ID_EMPTY(BAD_REQUEST,"UserId가 필요합니다."),

    //공통 에러
    INVALID_REQUEST(BAD_REQUEST, "잘못된 요청입니다"),
    INVALID_PARAMETER(BAD_REQUEST, "잘못된 파라미터입니다."),
    RESOURCE_NOT_FOUND(NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "예상치 못한 오류가 발생했습니다."),

    // 인증 관련 에러
    AUTH_INVALID_TOKEN(UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    AUTH_EXPIRED_TOKEN(UNAUTHORIZED, "만료된 토큰입니다."),
    AUTH_INVALID_REFRESH_TOKEN(UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    AUTH_EXPIRED_REFRESH_TOKEN(UNAUTHORIZED, "만료된 리프레시 토큰입니다."),
    AUTH_REFRESH_TOKEN_NOT_FOUND(UNAUTHORIZED, "리프레시 토큰이 없습니다."),
    AUTH_TOKEN_NOT_FOUND(UNAUTHORIZED, "인증 토큰이 없습니다."),
    AUTH_UNAUTHORIZED(UNAUTHORIZED, "인증되지 않은 사용자입니다."),
    AUTH_ACCESS_DENIED(FORBIDDEN, "접근 권한이 없습니다."),
    AUTH_HEADER_MISSING(UNAUTHORIZED, "Authorization 헤더가 필요합니다."),
    AUTH_INVALID_TOKEN_TYPE(UNAUTHORIZED, "올바르지 않은 토큰 타입입니다."),

    // JWT 관련 에러
    JWT_INVALID_SIGNATURE(UNAUTHORIZED, "JWT 서명이 유효하지 않습니다."),
    JWT_MALFORMED(UNAUTHORIZED, "잘못된 형식의 JWT입니다."),
    JWT_UNSUPPORTED(UNAUTHORIZED, "지원하지 않는 JWT입니다."),
    JWT_CLAIMS_EMPTY(UNAUTHORIZED, "JWT claims가 비어있습니다."),
    JWT_EXPIRED(UNAUTHORIZED, "JWT 토큰이 만료되었습니다."),

    // 리프레쉬 토큰 에러
    REFRESH_TOKEN_SAVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "리프레시 토큰 저장에 실패했습니다."),
    REFRESH_TOKEN_DELETE_ERROR(BAD_REQUEST, "토큰 삭제에 실패했습니다."),

    // Steam API
    STEAM_API_REQUEST_FAILED(HttpStatus.BAD_GATEWAY, "Steam API 요청에 실패했습니다."),
    STEAM_API_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "Steam API 응답 시간이 초과되었습니다."),
    STEAM_INVALID_API_KEY(HttpStatus.UNAUTHORIZED, "유효하지 않은 Steam API 키입니다."),
    STEAM_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "Steam 사용자를 찾을 수 없습니다."),

    // Steam OpenID
    AUTH_OPENID_INVALID_PARAMS(HttpStatus.BAD_REQUEST, "OpenID 파라미터가 유효하지 않습니다."),
    AUTH_OPENID_VALIDATION_FAILED(HttpStatus.UNAUTHORIZED, "OpenID 검증에 실패했습니다."),
    AUTH_OPENID_REQUIRED_PARAM_MISSING(HttpStatus.BAD_REQUEST, "필수 OpenID 파라미터가 누락되었습니다."),
    AUTH_INVALID_STEAM_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 Steam ID 형식입니다."),

    // Steam Profile
    STEAM_INVALID_VISIBILITY_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 프로필 공개 설정 코드입니다."),
    STEAM_INVALID_PERSONA_STATE(HttpStatus.BAD_REQUEST, "유효하지 않은 Steam 상태 코드입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
