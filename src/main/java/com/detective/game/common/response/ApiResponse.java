package com.detective.game.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiResponse<T> {
    private static final String API_SUCCESS_MESSAGE = "API 요청이 성공했습니다.";

    private Integer code;
    private String message;
    private T result;

    /**
     * 성공 응답 - 데이터와 함께
     */
    public static <T> ApiResponse<T> success(T result) {
        return new ApiResponse<>(OK.value(), API_SUCCESS_MESSAGE, result);
    }

    /**
     * 성공 응답 - 데이터 없음
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(OK.value(), API_SUCCESS_MESSAGE, null);
    }

    /**
     * 성공 응답 - 커스텀 메시지
     */
    public static <T> ApiResponse<T> success(String message, T result) {
        return new ApiResponse<>(OK.value(), message, result);
    }
    /**
     * 서버 오류 응답 - 커스텀 메시지
     */
    public static <T> ApiResponse<T> serverError(String message) {
        return new ApiResponse<>(BAD_REQUEST.value(), message, null);
    }

    /**
     * 인증 오류 응답
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
