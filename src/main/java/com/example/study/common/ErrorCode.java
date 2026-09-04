package com.example.study.common;

import lombok.Getter;

/**
 * 실패 사유.
 *
 * 응답 코드가 같아도 사유가 다르면 화면이 구분해야 하므로 값을 나눔.
 * 화면은 code 만 보고 분기함.
 */
@Getter
public enum ErrorCode {

    INVALID_INPUT(400, "입력값 확인 필요"),
    DUPLICATE_APPLICATION(400, "이미 신청한 모집글"),
    STUDY_CLOSED(400, "마감된 모집글"),
    DEADLINE_PASSED(400, "마감일이 지남"),
    SELF_APPLICATION(400, "자기 모집글"),
    CAPACITY_EXCEEDED(400, "정원 초과"),
    CAPACITY_BELOW_ACCEPTED(400, "정원이 수락 인원보다 작음"),
    ALREADY_PROCESSED(400, "이미 처리된 신청"),
    STUDY_NOT_CLOSED(400, "마감되지 않은 스터디"),
    DUPLICATE_REVIEW(400, "이미 작성한 후기"),
    UNAUTHORIZED(401, "인증 실패"),
    FORBIDDEN(403, "권한 부재"),
    NOT_FOUND(404, "대상 부재"),
    INTERNAL_ERROR(500, "잠시 후 다시 시도");

    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
