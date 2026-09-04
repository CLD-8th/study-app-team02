package com.example.study.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReviewRequest(

        @NotBlank(message = "내용은 필수")
        @Size(max = 500, message = "내용은 500자 이하")
        String content,

        @Min(value = 1, message = "평점은 1 이상")
        @Max(value = 5, message = "평점은 5 이하")
        int rating) {
}
