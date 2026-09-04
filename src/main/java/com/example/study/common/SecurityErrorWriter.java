package com.example.study.common;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 걸러내는 층에서 발생한 실패의 응답 작성기.
 *
 * 전역 처리기는 표현 계층까지 도달한 예외만 받음.
 * 토큰이 없거나 권한이 모자라 걸러내는 층에서 끝나면 그곳에는 닿지 않으며,
 * 지정하지 않으면 본문이 비어 있어 화면이 사유를 알 수 없음.
 */
@Component
public class SecurityErrorWriter {

    public AuthenticationEntryPoint entryPoint() {
        return (request, response, exception) -> write(response, ErrorCode.UNAUTHORIZED, "인증 필요");
    }

    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, exception) -> write(response, ErrorCode.FORBIDDEN, "권한 부재");
    }

    private void write(HttpServletResponse response, ErrorCode code, String message) throws IOException {
        response.setStatus(code.getStatus());
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(
                "{\"status\": %d, \"code\": \"%s\", \"message\": \"%s\", \"fields\": null}"
                        .formatted(code.getStatus(), code.name(), message));
    }
}
