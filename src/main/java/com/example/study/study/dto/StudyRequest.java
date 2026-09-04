package com.example.study.study.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * 모집글 등록 · 수정 요청 형태.
 *
 * 모집자를 담지 않음. 토큰에서 확인하며 받으면 위조가 가능함.
 */
public record StudyRequest(

        @NotBlank(message = "제목은 필수")
        @Size(max = 200, message = "제목은 200자 이하")
        String title,

        @NotBlank(message = "소개는 필수")
        String content,

        @Min(value = 1, message = "정원은 1 이상")
        int capacity,

        @NotNull(message = "마감일은 필수")
        @Future(message = "마감일은 오늘 이후여야 합니다")
        LocalDate deadline) {
}
