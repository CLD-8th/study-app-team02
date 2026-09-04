package com.example.study.study.dto;

import com.example.study.study.StudyPost;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 목록 응답 형태.
 *
 * 소개를 담지 않음. 화면에 표시하지 않으며 분량만 늘어남.
 */
public record StudyListResponse(
        Long id,
        String title,
        Long writerId,
        String writerNickname,
        int capacity,
        long acceptedCount,
        LocalDate deadline,
        String status,
        LocalDateTime createdAt) {

    public static StudyListResponse of(StudyPost post, long acceptedCount) {
        return new StudyListResponse(
                post.getId(),
                post.getTitle(),
                post.getWriter().getId(),
                post.getWriter().getNickname(),
                post.getCapacity(),
                acceptedCount,
                post.getDeadline(),
                post.getStatus().name(),
                post.getCreatedAt());
    }
}
