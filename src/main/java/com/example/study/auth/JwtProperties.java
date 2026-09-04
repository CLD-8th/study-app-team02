package com.example.study.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 토큰 설정.
 *
 * 서명 값과 수명을 설정에서 읽음. 코드에 박으면 판마다 바꿀 수 없음.
 */
@ConfigurationProperties(prefix = "study.jwt")
public record JwtProperties(
        String secret,
        long accessExpireMinutes,
        long refreshExpireDays) {
}
