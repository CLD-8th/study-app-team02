package com.example.study.application.dto;

import com.example.study.application.Application;

import java.time.LocalDateTime;

/**
 * 신청 응답 형태.
 *
 * 내 신청 목록에서 제목을 표시하므로 모집글 제목을 함께 담음.
 */
public record ApplicationResponse(
        Long id,
        Long studyPostId,
        String studyPostTitle,
        Long applicantId,
        String applicantNickname,
        String status,
        String message,
        LocalDateTime createdAt) {

    public static ApplicationResponse from(Application application) {
        return new ApplicationResponse(
                application.getId(),
                application.getStudyPost().getId(),
                application.getStudyPost().getTitle(),
                application.getApplicant().getId(),
                application.getApplicant().getNickname(),
                application.getStatus().name(),
                application.getMessage(),
                application.getCreatedAt());
    }
}
