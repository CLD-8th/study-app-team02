package com.example.study.member.dto;

import com.example.study.member.Member;

import java.time.LocalDateTime;

/**
 * 회원 응답 형태.
 *
 * 비밀번호를 담지 않음. 변환된 값이라도 응답에 넣지 않음.
 */
public record MemberResponse(
        Long id,
        String email,
        String nickname,
        LocalDateTime createdAt) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getEmail(),
                member.getNickname(), member.getCreatedAt());
    }
}
