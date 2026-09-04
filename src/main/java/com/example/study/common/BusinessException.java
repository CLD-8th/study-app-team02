package com.example.study.common;

import lombok.Getter;

/**
 * 업무 규칙 위반.
 *
 * 사유마다 예외 클래스를 만들지 않고 사유 값을 담아 넘김.
 * 처리기가 사유에서 응답 코드와 화면 분기 값을 꺼냄.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * 문구를 구체화할 때 사용.
     *
     * 대상 부재는 무엇의 대상인지 알려 주는 편이 나음.
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
