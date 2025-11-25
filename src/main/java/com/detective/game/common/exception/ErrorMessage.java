package com.detective.game.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

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
    STEAM_API_REQUEST_FAILED(BAD_GATEWAY, "Steam API 요청에 실패했습니다."),
    STEAM_API_TIMEOUT(GATEWAY_TIMEOUT, "Steam API 응답 시간이 초과되었습니다."),
    STEAM_INVALID_API_KEY(UNAUTHORIZED, "유효하지 않은 Steam API 키입니다."),
    STEAM_USER_NOT_FOUND(NOT_FOUND, "Steam 사용자를 찾을 수 없습니다."),

    // Steam Profile
    STEAM_INVALID_VISIBILITY_CODE(BAD_REQUEST, "유효하지 않은 프로필 공개 설정 코드입니다."),
    STEAM_INVALID_PERSONA_STATE(BAD_REQUEST, "유효하지 않은 Steam 상태 코드입니다."),

    //steam 언리얼 클라이언트
    AUTH_TICKET_REQUIRED(BAD_REQUEST, "AuthTicket이 필요합니다."),

    //AI 서버 응답
    AI_COMMUNICATION_FAILED(BAD_GATEWAY,"AI 서버와 통신 실패"),
    AI_BAD_RESPONSE(BAD_GATEWAY,"AI 서버에서 비정상 응답"),
    AI_EMPTY_RESPONSE(BAD_GATEWAY,"AI 서버 응답이 비어 있음"),

    //방 관련 오류
    MAX_PLAYER_INVALID(BAD_REQUEST, "최대 인원은 1~4명만 가능합니다."),
    NOT_WAITING_STATUS(BAD_REQUEST, "대기 중인 방이 아닙니다."),
    ROOM_IS_FULL(BAD_REQUEST, "방이 가득 찼습니다."),
    PASSWORD_MISMATCH(UNAUTHORIZED, "비밀번호가 올바르지 않습니다."),
    ALREADY_JOINED(BAD_REQUEST, "이미 방에 참여하고 있습니다."),
    ROOM_NOT_FOUND(NOT_FOUND, "해당 방을 찾을 수 없습니다."),
    NOT_HOST(FORBIDDEN, "방장이 아닙니다."),
    LOCK_FAILED_MESSAGE(CONFLICT,"방이 다른 요청에 의해 처리 중입니다."),
    ROOM_ALREADY_FINAL_SUBMITTED(CONFLICT,"\"이미 최종 보고서를 제출한 방입니다: \"");

    private final HttpStatus httpStatus;
    private final String message;
}
