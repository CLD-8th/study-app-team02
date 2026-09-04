package com.example.study.auth;

import com.example.study.auth.dto.TokenResponse;
import com.example.study.common.BusinessException;
import com.example.study.common.ErrorCode;
import com.example.study.member.Member;
import com.example.study.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    /**
     * 로그인.
     *
     * 이메일이 없는 경우와 비밀번호가 틀린 경우를 구분해 알리지 않음.
     * 구분하면 가입 여부가 드러남.
     */
    public TokenResponse login(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "인증 실패"));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "인증 실패");
        }

        log.info("로그인: id={}", member.getId());
        return new TokenResponse(
                tokenProvider.issueAccessToken(member.getId(), member.getRole().name()),
                tokenProvider.issueRefreshToken(member.getId()),
                member.getId(),
                member.getNickname());
    }

    /**
     * 접근 토큰 재발급.
     *
     * 갱신 토큰을 보관하지 않으므로 서명과 용도만 확인함.
     * 로그아웃해도 만료까지 유효하며 알고 남긴 문제임.
     */
    public TokenResponse reissue(String refreshToken) {
        if (!tokenProvider.isValid(refreshToken) || !tokenProvider.isRefreshToken(refreshToken)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "인증 실패");
        }
        Member member = memberRepository.findById(tokenProvider.getMemberId(refreshToken))
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "회원 부재"));

        return new TokenResponse(
                tokenProvider.issueAccessToken(member.getId(), member.getRole().name()),
                null,
                member.getId(),
                member.getNickname());
    }
}
