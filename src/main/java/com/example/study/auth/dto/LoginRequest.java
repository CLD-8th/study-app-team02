package com.example.study.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "이메일은 필수")
        String email,

        @NotBlank(message = "비밀번호는 필수")
        String password) {
}
