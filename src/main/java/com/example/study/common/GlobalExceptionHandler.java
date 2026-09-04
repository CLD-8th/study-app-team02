package com.example.study.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 전역 예외 처리기.
 *
 * 표현 계층이 예외를 잡지 않아도 여기서 받아 같은 형태로 변환함.
 * 위에서부터 대조하며 첫 일치에서 멈추므로 좁은 예외를 먼저 배치함.
 *
 * 걸러내는 층에서 끝난 실패는 여기에 도달하지 않으며
 * 그 경우는 진입점과 거부 처리기가 담당함.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException e) {
        ErrorCode code = e.getErrorCode();
        log.debug("업무 규칙 위반: {} · {}", code.name(), e.getMessage());

        return ResponseEntity.status(code.getStatus())
                .body(ErrorResponse.of(code, e.getMessage()));
    }

    /**
     * 검증 표기 위반.
     *
     * 어느 항목이 왜 틀렸는지를 함께 담아 화면이 입력란마다 표시할 수 있게 함.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> fields = new LinkedHashMap<>();
        e.getFieldErrors().forEach(error -> fields.put(error.getField(), error.getDefaultMessage()));

        log.debug("검증 실패: {}", fields);
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(ErrorCode.INVALID_INPUT, ErrorCode.INVALID_INPUT.getMessage(), fields));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegal(IllegalArgumentException e) {
        log.debug("입력값 오류: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(ErrorCode.INVALID_INPUT, e.getMessage()));
    }

    /**
     * 위에서 걸리지 않은 나머지.
     *
     * 예상하지 못한 상황이므로 error 수준으로 남기며 예외를 함께 넘겨
     * 발생 위치가 기록되도록 함. 사용자에게는 내부 사정을 알리지 않음.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
        log.error("처리하지 못한 예외", e);
        return ResponseEntity.internalServerError()
                .body(ErrorResponse.of(ErrorCode.INTERNAL_ERROR, ErrorCode.INTERNAL_ERROR.getMessage()));
    }
}
