package com.example.study.review.dto;

import com.example.study.review.Review;

import java.time.LocalDateTime;

/**
 * 후기 응답 형태.
 *
 * writerId 를 담음. 화면이 자기 후기에만 삭제 단추를 노출하기 위해 필요함.
 */
public record ReviewResponse(
        Long id,
        Long studyPostId,
        String content,
        int rating,
        Long writerId,
        String writerNickname,
        LocalDateTime createdAt) {

    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getStudyPost().getId(),
                review.getContent(),
                review.getRating(),
                review.getWriter().getId(),
                review.getWriter().getNickname(),
                review.getCreatedAt());
    }
}
