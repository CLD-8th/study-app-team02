package com.example.study.application;

/**
 * 신청 상태.
 *
 * 수락과 거절은 되돌릴 수 없음.
 * CANCELED 는 취소를 기록으로 남기는 팀이 쓰는 값이며 기준 구현에서는 행을 삭제함.
 */
public enum ApplicationStatus {
    PENDING, ACCEPTED, REJECTED, CANCELED
}
