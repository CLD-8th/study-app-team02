package com.example.study.application;

import com.example.study.application.dto.ApplicationResponse;
import com.example.study.common.BusinessException;
import com.example.study.common.ErrorCode;
import com.example.study.member.Member;
import com.example.study.member.MemberService;
import com.example.study.study.StudyPost;
import com.example.study.study.StudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 신청 업무 계층.
 *
 * 판단 순서가 중요함. 대상 확인을 먼저 하지 않으면
 * 없는 모집글에 대해 다른 판단을 시도하게 됨.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final StudyService studyService;
    private final MemberService memberService;

    /**
     * 신청.
     *
     * 순서는 대상 확인 · 자기 모집글 · 상태 · 마감일 · 중복임.
     * 상태가 마감인 경우와 마감일이 지난 경우는 사유가 다르므로 나누어 판단함.
     */
    @Transactional
    public ApplicationResponse apply(Long studyPostId, String message, Long memberId) {
        // 1. 신청할 모집글과 모집자 정보 조회
        StudyPost post = studyService.getWithWriter(studyPostId);

        // 2. 모집자 본인이 작성한 모집글엔 참여 신청 못함.
        if(post.isWrittenBy(memberId)) {
            throw new BusinessException(
                    ErrorCode.SELF_APPLICATION,
                    "자기 모집글에는 신청할 수 없음"
            );
        }

        // 3. 모집상태 -> recruiting?
        // 모집상태가 리크루팅이니?
        if (!post.isRecruiting()) {
            throw new BusinessException(
                    ErrorCode.STUDY_CLOSED,
                    "마감된 모집글에는 신청할 수 없음"
            );
        }

        // 4. 모집 상태가 리크루팅 상태여도 마감일 지나면 신청 불가
        if(post.isDeadlinePassed()) {
            throw new BusinessException(
                    ErrorCode.DEADLINE_PASSED,
                    "마감일이 지난 모집글에는 신청할 수 없음"
            );
        }

        // 5. 같은 회원이 중복으로 모집글에 신청했나?
        if(applicationRepository
                .findByStudyPostIdAndApplicantId(studyPostId, memberId)
                .isPresent()) {
            throw new BusinessException(
                    ErrorCode.DUPLICATE_APPLICATION,
                    "이미 신청한 모집글"
            );
        }

        // 6. 로그인한 회원 번호로 실제 신청자 정보를 조회함.
        Member applicant = memberService.getMember(memberId);

        // 7. 신청 정보를 생성
        Application application = new Application(post, applicant, message);

        // 8. 만든 신청 정보를 DB에 저장
        Application saved = applicationRepository.save(application);

        // 9. 신청 처리 결과를 서버 실행 기록에 남기기
        log.info (
                "스터디 신청: id={}, studyPostId={}, applicantId={}",
                saved.getId(),
                studyPostId,
                memberId

        );

        // 10. 엔티티를 API 응답 형태로 변환해 요청한 화면에 반환
        return ApplicationResponse.from(saved);


    }

    /**
     * 신청 취소.
     *
     * 대기 상태만 취소 가능함. 수락된 신청을 취소하면
     * 마감된 모집글에 빈자리가 생기며 되돌릴 방법이 없음.
     */
    @Transactional
    public void cancel(Long applicationId, Long memberId) {
        // 1. 취소 신청 part와 해당 모집글을 함께 조회
        Application application = getWithStudyPost(applicationId);

        // 2. 로그인한 회원이 해당 신청 작성자인지? 본인 확인
        if (!application.isAppliedBy(memberId)) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN,
                    "신청자 본인만 취소할 수 있음"
            );
        }

        // 3. 아직 처리 X 펜딩 상태의 신청인지 확인
        if (!application.isPending()) {
            throw new BusinessException(
                    ErrorCode.ALREADY_PROCESSED,
                    "이미 처리된 신청은 취소할 수 없음"
            );
        }

        // 4. 취소된 신청 기록은 신청 행을 삭제
        applicationRepository.delete(application);

        // 5. 취소된 신청/신청자 번호를 서버 실행 기록에 남김
        log.info(
                "스터디 신청 취소: applicationId={}, applicantId={}",
                applicationId,
                memberId
        );


    }

    /**
     * 신청 목록을 조회함.
     *
     * 모집자 본인만 볼 수 있음.
     *
     * @param studyPostId 모집글 식별자
     * @param memberId 토큰에서 확인한 회원 식별자
     * @return 신청 목록
     * @throws BusinessException 모집글이 없으면 404, 모집자가 아니면 403
     */
    public List<ApplicationResponse> findByStudy(Long studyPostId, Long memberId) {
        StudyPost studyPost = studyService.getWithWriter(studyPostId);
        if (!studyPost.isWrittenBy(memberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "모집자만 조회 가능");
        }
        return applicationRepository.findByStudyPostIdOrderByCreatedAtAsc(studyPostId).stream()
                .map(ApplicationResponse::from)
                .toList();
    }

    public List<ApplicationResponse> findMine(Long memberId) {
    /*
     * TODO 64 · 내 신청 조회
     *
     * 기능        토큰에서 온 식별자로 내 신청을 최신순으로 조회함
     * 활용메소드  ApplicationRepository 의 내 신청 규약   TODO 62 · 같은 담당
     *             ApplicationResponse.from()           제공됨
     * 반환형태    List<ApplicationResponse>
     * 동작결과    EP-17 · 상세 화면의 신청 구획도 이 값을 씀
     */
        return applicationRepository.findByApplicantIdOrderByCreatedAtDesc(memberId)
                .stream()
                .map(ApplicationResponse::from)
                .toList();
    }

    /**
     * 신청을 수락함.
     *
     * 마지막 자리를 채우면 모집글도 함께 마감함. 별도 처리를 두지 않고 수락 시점에 판단함.
     *
     * @param applicationId 신청 식별자
     * @param memberId 토큰에서 확인한 회원 식별자
     * @return 수락된 신청
     * @throws BusinessException 대상이 없으면 404, 모집자가 아니면 403,
     *         이미 처리됐으면 400, 정원이 차 있으면 400
     */
    @Transactional
    public ApplicationResponse accept(Long applicationId, Long memberId) {
        Application application = processable(applicationId, memberId);
        StudyPost studyPost = application.getStudyPost();

        long accepted = applicationRepository.countByStudyPostIdAndStatus(
                studyPost.getId(), ApplicationStatus.ACCEPTED);
        if (accepted >= studyPost.getCapacity()) {
            throw new BusinessException(ErrorCode.CAPACITY_EXCEEDED, "정원 초과");
        }

        application.accept();
        if (accepted + 1 == studyPost.getCapacity()) {
            studyPost.close();
        }

        log.info("신청 수락: id={}", applicationId);
        return ApplicationResponse.from(application);
    }

    /**
     * 신청을 거절함.
     *
     * 정원을 확인하지 않음. 거절은 인원에 영향을 주지 않음.
     *
     * @param applicationId 신청 식별자
     * @param memberId 토큰에서 확인한 회원 식별자
     * @return 거절된 신청
     * @throws BusinessException 대상이 없으면 404, 모집자가 아니면 403, 이미 처리됐으면 400
     */
    @Transactional
    public ApplicationResponse reject(Long applicationId, Long memberId) {
        Application application = processable(applicationId, memberId);
        application.reject();

        log.info("신청 거절: id={}", applicationId);
        return ApplicationResponse.from(application);
    }

    /**
     * 수락·거절 처리가 가능한지 확인함.
     *
     * 모집자 본인만, 대기 상태의 신청만 처리 가능함.
     *
     * @param applicationId 신청 식별자
     * @param memberId 토큰에서 확인한 회원 식별자
     * @return 처리 대상 신청
     * @throws BusinessException 대상이 없으면 404, 모집자가 아니면 403, 이미 처리됐으면 400
     */
    private Application processable(Long applicationId, Long memberId) {
        Application application = getWithStudyPost(applicationId);
        if (!application.getStudyPost().isWrittenBy(memberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "모집자만 처리 가능");
        }
        if (!application.isPending()) {
            throw new BusinessException(ErrorCode.ALREADY_PROCESSED, "이미 처리된 신청");
        }
        return application;
    }

    private Application getWithStudyPost(Long id) {
        // 제공 · 담당 4 도 이 메서드를 씀.
        return applicationRepository.findWithStudyPostById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "신청 부재"));
    }
}
