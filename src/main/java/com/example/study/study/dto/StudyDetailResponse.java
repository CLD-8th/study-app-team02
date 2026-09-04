package com.example.study.study.dto;

import com.example.study.study.StudyPost;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 상세 응답 형태.
 *
 * 후기 건수를 담지 않음. 후기는 별도 조회이며 서로 다른 담당의 영역임.
 */
public record StudyDetailResponse(
        Long id,
        String title,
        String content,
        int capacity,
        long acceptedCount,
        LocalDate deadline,
        String status,
        Long writerId,
        String writerNickname,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static StudyDetailResponse of(StudyPost post, long acceptedCount) {
        return new StudyDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCapacity(),
                acceptedCount,
                post.getDeadline(),
                post.getStatus().name(),
                post.getWriter().getId(),
                post.getWriter().getNickname(),
                post.getCreatedAt(),
                post.getUpdatedAt());
    }
}
