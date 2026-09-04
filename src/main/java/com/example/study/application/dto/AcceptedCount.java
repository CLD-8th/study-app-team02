package com.example.study.application.dto;

/**
 * 모집글별 수락 인원.
 *
 * 목록에서 건마다 세면 조회 구문이 건수에 비례함.
 * 식별자 묶음을 한 번에 세어 이 형태로 받음.
 */
public record AcceptedCount(Long studyPostId, long count) {
}
