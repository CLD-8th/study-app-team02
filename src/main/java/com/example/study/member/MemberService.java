package com.example.study.member;

import com.example.study.common.BusinessException;
import com.example.study.common.ErrorCode;
import com.example.study.member.dto.MemberResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 가입.
     *
     * 이메일 중복은 조회가 필요하므로 표기로 두지 못하고 여기서 확인함.
     * 화면이 이메일란 아래에 표시할 수 있도록 항목별 사유로 담아 던짐.
     */
    @Transactional
    public MemberResponse join(String email, String password, String nickname) {
        if (memberRepository.existsByEmail(email)) {
            Map<String, String> fields = new LinkedHashMap<>();
            fields.put("email", "이미 가입된 이메일입니다");
            throw new DuplicateEmailException(fields);
        }
        Member saved = memberRepository.save(
                new Member(email, passwordEncoder.encode(password), nickname));

        log.info("회원 가입: id={}", saved.getId());
        return MemberResponse.from(saved);
    }

    public MemberResponse findById(Long id) {
        return MemberResponse.from(getMember(id));
    }

    public Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "회원 부재"));
    }
}
