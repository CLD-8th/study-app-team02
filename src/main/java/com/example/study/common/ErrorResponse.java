package com.example.study.common;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 실패 응답 형태.
 *
 * 모든 실패가 같은 형태로 반환되므로 화면은 code 하나만 보고 분기 가능함.
 *
 * @param status    응답 코드
 * @param code      화면이 분기할 기준
 * @param message   사용자에게 표시할 문구
 * @param fields    항목별 실패 사유 · 검증 실패에만 채워짐
 * @param timestamp 발생 시각
 */
public record ErrorResponse(
        int status,
        String code,
        String message,
        Map<String, String> fields,
        LocalDateTime timestamp) {

    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(errorCode.getStatus(), errorCode.name(), message, null, LocalDateTime.now());
    }

    public static ErrorResponse of(ErrorCode errorCode, String message, Map<String, String> fields) {
        return new ErrorResponse(errorCode.getStatus(), errorCode.name(), message, fields, LocalDateTime.now());
    }
}
