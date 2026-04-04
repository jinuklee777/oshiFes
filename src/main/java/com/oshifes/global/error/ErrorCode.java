package com.oshifes.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON-001", "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-002", "서버 오류가 발생했습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON-003", "요청한 리소스를 찾을 수 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON-004", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON-005", "접근 권한이 없습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-001", "존재하지 않는 사용자입니다."),

    // Event
    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "EVENT-001", "존재하지 않는 이벤트입니다."),

    // Pilgrimage
    PILGRIMAGE_PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "PILGRIMAGE-001", "존재하지 않는 성지순례 계획입니다."),
    PILGRIMAGE_SPOT_NOT_FOUND(HttpStatus.NOT_FOUND, "PILGRIMAGE-002", "존재하지 않는 성지순례 장소입니다."),

    // Review
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW-001", "존재하지 않는 리뷰입니다."),

    // IP
    IP_TITLE_NOT_FOUND(HttpStatus.NOT_FOUND, "IP-001", "존재하지 않는 IP 타이틀입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
