package com.example.study.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 토큰 발급과 해석.
 *
 * 접근 토큰과 갱신 토큰을 용도로 구분함.
 * 내용은 감춰지지 않으므로 비밀번호나 개인 정보를 담지 않음.
 */
@Slf4j
@Component
public class TokenProvider {

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    private final SecretKey key;
    private final long accessExpireMillis;
    private final long refreshExpireMillis;

    public TokenProvider(JwtProperties properties) {
        this.key = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
        this.accessExpireMillis = properties.accessExpireMinutes() * 60 * 1000;
        this.refreshExpireMillis = properties.refreshExpireDays() * 24 * 60 * 60 * 1000;
    }

    public String issueAccessToken(Long memberId, String role) {
        return build(memberId, TYPE_ACCESS, accessExpireMillis).claim(CLAIM_ROLE, role).compact();
    }

    public String issueRefreshToken(Long memberId) {
        return build(memberId, TYPE_REFRESH, refreshExpireMillis).compact();
    }

    private io.jsonwebtoken.JwtBuilder build(Long memberId, String type, long expireMillis) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim(CLAIM_TYPE, type)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expireMillis))
                .signWith(key);
    }

    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            log.debug("토큰 확인 실패: {}", e.getClass().getSimpleName());
            return false;
        }
    }

    public Long getMemberId(String token) {
        return Long.valueOf(parse(token).getSubject());
    }

    public String getRole(String token) {
        return parse(token).get(CLAIM_ROLE, String.class);
    }

    public boolean isAccessToken(String token) {
        return TYPE_ACCESS.equals(parse(token).get(CLAIM_TYPE, String.class));
    }

    public boolean isRefreshToken(String token) {
        return TYPE_REFRESH.equals(parse(token).get(CLAIM_TYPE, String.class));
    }

    private Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
