package com.example.study.auth.dto;

/**
 * 로그인 응답 형태.
 *
 * memberId 가 화면의 분기 조건임. 상세 응답의 writerId 와 비교해
 * 수정 단추 · 신청 영역 · 신청 목록 노출을 가름.
 */
public record TokenResponse(
        String accessToken,
        String refreshToken,
        Long memberId,
        String nickname) {
}
