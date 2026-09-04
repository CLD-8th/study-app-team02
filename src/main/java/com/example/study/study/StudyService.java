package com.example.study.study;

import com.example.study.application.ApplicationRepository;
import com.example.study.application.ApplicationStatus;
import com.example.study.application.dto.AcceptedCount;
import com.example.study.common.BusinessException;
import com.example.study.common.ErrorCode;
import com.example.study.member.Member;
import com.example.study.member.MemberService;
import com.example.study.study.dto.StudyDetailResponse;
import com.example.study.study.dto.StudyListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 모집글 업무 계층.
 *
 * 소유 관계와 상태 전이를 여기서 판단함.
 * 값의 형식과 범위는 요청 형태에서 이미 걸러짐.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyService {

    private final StudyPostRepository studyPostRepository;
    private final ApplicationRepository applicationRepository;
    private final MemberService memberService;

    /**
     * 모집글을 등록함.
     *
     * @param title 제목
     * @param content 내용
     * @param capacity 정원
     * @param deadline 마감일
     * @param memberId 토큰에서 확인한 회원 식별자, 모집자가 됨
     * @return 등록된 모집글
     */
    @Transactional
    public StudyDetailResponse create(String title, String content, int capacity,
                                      LocalDate deadline, Long memberId) {
        Member writer = memberService.getMember(memberId);
        StudyPost saved = studyPostRepository.save(
                new StudyPost(title, content, capacity, deadline, writer));

        log.info("모집글 등록: id={} · writerId={}", saved.getId(), memberId);
        // 갓 만든 글이므로 수락 인원은 0.
        return StudyDetailResponse.of(saved, 0);
    }

    /**
     * 목록 조회.
     *
     * 수락 인원을 건마다 세면 조회 구문이 건수에 비례함.
     * 식별자 묶음을 한 번에 세어 붙임.
     */
    public Page<StudyListResponse> findAll(String keyword, StudyStatus status, Pageable pageable) {
    /*
     * TODO 11 · 모집글 목록 조회
     *
     * 기능        검색어가 비어 있으면 조건에서 빼고 조회함
     *             수락 인원을 건마다 세지 않고 식별자 묶음으로 한 번에 세어 붙임
     * 활용메소드  StudyPostRepository.search()   제공됨
     *             StudyService.acceptedCounts()  같은 클래스 · 제공됨
     *             StudyListResponse.of()         제공됨
     *             Page.map()                     쪽 객체의 내용만 변환
     * 반환형태    Page<StudyListResponse>
     * 동작결과    EP-01 · 목록이 열 건이어도 조회 구문은 둘
     */
        throw new UnsupportedOperationException("TODO 11");
    }

    /**
     * 모집글 상세를 조회함. 손님도 볼 수 있음.
     *
     * @param id 모집글 식별자
     * @return 모집글 상세
     * @throws BusinessException 대상이 없으면 404
     */
    public StudyDetailResponse findById(Long id) {
        StudyPost post = getWithWriter(id);
        return StudyDetailResponse.of(post, countAccepted(id));
    }

    /**
     * 모집글을 수정함.
     *
     * 마감된 모집글은 수정하지 않음. 정원을 늘리면 자리가 있는데 신청이 막히고
     * 마감일을 바꿔도 상태가 그대로라 의미가 없음.
     *
     * 정원은 현재 수락 인원보다 작게 바꿀 수 없음.
     * 인원이 정원을 넘는 상태가 되며 되돌릴 방법이 없음.
     *
     * @param id 모집글 식별자
     * @param title 제목
     * @param content 내용
     * @param capacity 정원
     * @param deadline 마감일
     * @param memberId 토큰에서 확인한 회원 식별자
     * @return 수정된 모집글
     * @throws BusinessException 대상이 없으면 404, 모집자가 아니면 403,
     *         마감된 모집글이면 400, 정원이 수락 인원보다 작으면 400
     */
    @Transactional
    public StudyDetailResponse update(Long id, String title, String content, int capacity,
                                      LocalDate deadline, Long memberId) {
        StudyPost post = getWithWriter(id);

        if (!post.isWrittenBy(memberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "모집자만 수정 가능");
        }
        if (!post.isRecruiting()) {
            throw new BusinessException(ErrorCode.STUDY_CLOSED, "마감된 모집글은 수정 불가");
        }

        long accepted = countAccepted(id);
        if (capacity < accepted) {
            throw new BusinessException(ErrorCode.CAPACITY_BELOW_ACCEPTED,
                    "정원은 현재 수락 인원 " + accepted + "명보다 작을 수 없음");
        }

        post.update(title, content, capacity, deadline);

        log.info("모집글 수정: id={}", id);
        return StudyDetailResponse.of(post, accepted);
    }

    /**
     * 모집글을 삭제함.
     *
     * @param id 모집글 식별자
     * @param memberId 토큰에서 확인한 회원 식별자
     * @throws BusinessException 대상이 없으면 404, 모집자가 아니면 403
     */
    @Transactional
    public void delete(Long id, Long memberId) {
        StudyPost post = getWithWriter(id);

        if (!post.isWrittenBy(memberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "모집자만 삭제 가능");
        }
        studyPostRepository.delete(post);

        log.info("모집글 삭제: id={}", id);
    }

    /**
     * 모집글을 마감함.
     *
     * 대기 상태의 신청은 그대로 둠. 모집자가 개별로 처리함.
     *
     * @param id 모집글 식별자
     * @param memberId 토큰에서 확인한 회원 식별자
     * @return 마감된 모집글
     * @throws BusinessException 대상이 없으면 404, 모집자가 아니면 403, 이미 마감이면 400
     */
    @Transactional
    public StudyDetailResponse close(Long id, Long memberId) {
        StudyPost post = getWithWriter(id);

        if (!post.isWrittenBy(memberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "모집자만 마감 가능");
        }
        if (!post.isRecruiting()) {
            throw new BusinessException(ErrorCode.STUDY_CLOSED, "이미 마감된 모집글");
        }

        // 상태 전이는 내용 수정이 아니므로 updatedAt 을 건드리지 않음.
        post.close();

        log.info("모집 마감: id={}", id);
        return StudyDetailResponse.of(post, countAccepted(id));
    }

    public List<StudyListResponse> findMine(Long memberId) {
    /*
     * TODO 63 · 내 모집글 조회
     *
     * 기능        토큰에서 온 식별자로 내 모집글을 최신순으로 조회함
     *             목록과 마찬가지로 수락 인원을 한 번에 세어 붙임
     * 활용메소드  StudyPostRepository 의 내 모집글 규약   TODO 61 · 같은 담당
     *             StudyService.acceptedCounts()          같은 클래스 · 제공됨
     *             StudyListResponse.of()                 제공됨
     * 반환형태    List<StudyListResponse>
     * 동작결과    EP-16 · 내가 등록한 것만 최신순으로 나옴
     */
        List<StudyPost> posts =
                studyPostRepository.findByWriterIdOrderByCreatedAtDesc(memberId);

        List<Long> ids = posts.stream()
                .map(StudyPost::getId)
                .toList();

        Map<Long, Long> counts = acceptedCounts(ids);

        return posts.stream()
                .map(post -> StudyListResponse.of(
                        post,
                        counts.getOrDefault(post.getId(), 0L)
                ))
                .toList();
    }

    public StudyPost getWithWriter(Long id) {
        // 제공 · 담당 3 · 4 · 5 도 이 메서드를 씀.
        return studyPostRepository.findWithWriterById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "모집글 부재"));
    }

    private long countAccepted(Long studyPostId) {
        return applicationRepository.countByStudyPostIdAndStatus(studyPostId, ApplicationStatus.ACCEPTED);
    }

    private Map<Long, Long> acceptedCounts(List<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        return applicationRepository.countAcceptedByStudyPostIds(ids, ApplicationStatus.ACCEPTED)
                .stream()
                .collect(Collectors.toMap(AcceptedCount::studyPostId, AcceptedCount::count));
    }
}
