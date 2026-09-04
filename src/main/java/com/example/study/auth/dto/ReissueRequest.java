package com.example.study.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record ReissueRequest(

        @NotBlank(message = "갱신 토큰은 필수")
        String refreshToken) {
}
