package com.example.study.member;

import com.example.study.common.BusinessException;
import com.example.study.common.ErrorCode;
import lombok.Getter;

import java.util.Map;

/**
 * 이메일 중복.
 *
 * 화면이 이메일 입력란 아래에 표시하도록 항목별 사유를 함께 담음.
 * 검증 표기 위반과 응답 형태가 같아지므로 화면이 한 가지 방식으로 처리 가능함.
 */
@Getter
public class DuplicateEmailException extends BusinessException {

    private final Map<String, String> fields;

    public DuplicateEmailException(Map<String, String> fields) {
        super(ErrorCode.INVALID_INPUT, "입력값 확인 필요");
        this.fields = fields;
    }
}
