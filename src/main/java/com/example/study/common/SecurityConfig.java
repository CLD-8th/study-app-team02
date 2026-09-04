package com.example.study.common;

import com.example.study.auth.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 접근 규칙.
 *
 * 순서대로 대조하므로 개별 규칙을 먼저 두고 전체 대상을 마지막에 배치함.
 * 걸러내는 층에서 끝난 요청은 전역 처리기에 도달하지 않으므로
 * 실패 응답을 여기서 따로 지정함.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SecurityErrorWriter securityErrorWriter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 토큰으로 신원을 확인하므로 다른 사이트에서 온 요청을 막는 장치는 불필요함.
                .csrf(csrf -> csrf.disable())
                // 신원을 보관하지 않고 요청마다 토큰으로 확인함.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 화면과 정적 자원은 공개.
                        .requestMatchers("/", "/*.html", "/css/**", "/js/**", "/img/**", "/favicon.ico").permitAll()
                        // 가입과 로그인은 토큰이 없는 상태에서 부름.
                        .requestMatchers(HttpMethod.POST, "/api/members", "/api/auth/login", "/api/auth/reissue").permitAll()
                        // 조회는 손님도 가능.
                        .requestMatchers(HttpMethod.GET, "/api/studies", "/api/studies/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/studies/*/reviews", "/api/members/*").permitAll()
                        // 나머지는 인증 필요.
                        .anyRequest().authenticated())
                // 걸러내는 층의 실패도 같은 형태로 응답.
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(securityErrorWriter.entryPoint())
                        .accessDeniedHandler(securityErrorWriter.accessDeniedHandler()))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * 비밀번호 변환기.
     *
     * 방식 표시가 앞에 붙으므로 나중에 방식을 바꿔도 기존 값을 확인할 수 있음.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
