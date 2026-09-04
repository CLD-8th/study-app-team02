package com.example.study.application;

import com.example.study.application.dto.ApplicationRequest;
import com.example.study.application.dto.ApplicationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 신청 표현 계층.
 *
 * 대상 모집글에 종속된 주소와 신청 자체를 가리키는 주소가 섞여 있어
 * 묶음 주소를 클래스에 두지 않음.
 */
@RestController
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    /*
     * 스터디 참여 신청 요청을 처리한다.
     *
     * 모집글 번호는 URL 경로에서 받고, 신청 메시지는 요청 본문에서 받는다.
     * 신청자 번호는 사용자가 직접 입력한 값이 아니라 인증된 토큰에서 가져온다.
     *
     * 신청이 정상적으로 생성되면 HTTP 201 Created와 신청 정보를 반환한다.
     */
    @PostMapping("/api/studies/{studyId}/applications")
    public ResponseEntity<ApplicationResponse> apply(
            @PathVariable Long studyId,
            @Valid @RequestBody ApplicationRequest request,
            @AuthenticationPrincipal Long memberId
    ) {
        // TODO 31에서 구현한 신청 서비스를 호출
        // 메시지 길이 검증은 @Valid를 통해 서비스 호출 전에 처리된다.
        ApplicationResponse created = applicationService.apply(
                studyId,
                request.message(),
                memberId
        );

        // 새로운 신청이 생성됐으니 HTTP 201 Created와 신청 정보를 반환
        return ResponseEntity.status(201).body(created);
    }

    /*
     * 대기 상태의 참여 신청 취소 요청을 처리한다.
     *
     * 취소할 신청 번호는 URL 경로에서 받고,
     * 신청자 번호는 인증된 토큰에서 가져온다.
     *
     * 취소가 완료되면 반환할 내용이 없으므로 HTTP 204 No Content를 반환한다.
     */

    @DeleteMapping("/api/applications/{id}")
    public ResponseEntity<Void> cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal Long memberId
    ) {
        // TODO 32에서 구현한 신청 취소 서비스를 호출함
        // 신청자 본인 및 PENDING 상태 여부는 서비스 계층에서 판단함
        applicationService.cancel(id, memberId);

        // 신청이 삭제됐고 반환할게 없으니 HTTP 204 응답
        return ResponseEntity.noContent().build();
    }

    /*
     * TODO 46 · 신청 처리 주소 셋
     *
     * 기능        GET /api/studies/{studyId}/applications
     *             PATCH /api/applications/{id}/accept
     *             PATCH /api/applications/{id}/reject 를 만듦
     * 활용메소드  ApplicationService.findByStudy()   TODO 42 · 같은 담당
     *             ApplicationService.accept()       TODO 43 · 같은 담당
     *             ApplicationService.reject()       TODO 44 · 같은 담당
     * 반환형태    List<ApplicationResponse> · ApplicationResponse
     * 동작결과    EP-09 · EP-10 · EP-11
     */
}
