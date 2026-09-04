package com.example.study.application.dto;

import jakarta.validation.constraints.Size;

public record ApplicationRequest(

        @Size(max = 300, message = "신청 메시지는 300자 이하")
        String message) {
}
