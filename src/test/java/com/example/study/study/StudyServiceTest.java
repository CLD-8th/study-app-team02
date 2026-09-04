package com.example.study.study;

import com.example.study.application.ApplicationRepository;
import com.example.study.application.ApplicationStatus;
import com.example.study.application.dto.AcceptedCount;
import com.example.study.member.Member;
import com.example.study.member.MemberService;
import com.example.study.study.dto.StudyListResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StudyServiceTest {

    @Mock
    private StudyPostRepository studyPostRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private StudyService studyService;

    private StudyPost createPost(Long id, String title, int capacity, LocalDate deadline, StudyStatus status, Member writer) {
        StudyPost post = new StudyPost(title, "소개 내용", capacity, deadline, writer);
        ReflectionTestUtils.setField(post, "id", id);
        ReflectionTestUtils.setField(post, "status", status);
        return post;
    }

    private Member createMember(Long id, String email, String nickname) {
        Member member = new Member(email, "password", nickname);
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    @Test
    @DisplayName("TODO 11 · 검색어와 상태가 없으면 전체 목록을 조회하고 수락 인원을 매핑한다 (NFR-04)")
    void findAll_WithoutKeywordAndStatus_ReturnsPageWithAcceptedCounts() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        Member writer = createMember(1L, "hong@example.com", "홍길동");
        StudyPost post1 = createPost(1L, "자바 스터디 모집", 5, LocalDate.of(2026, 9, 30), StudyStatus.RECRUITING, writer);
        StudyPost post2 = createPost(2L, "스프링 스터디 모집", 4, LocalDate.of(2026, 10, 15), StudyStatus.RECRUITING, writer);

        Page<StudyPost> postPage = new PageImpl<>(List.of(post1, post2), pageable, 2);
        given(studyPostRepository.search(null, null, pageable)).willReturn(postPage);

        List<AcceptedCount> acceptedCounts = List.of(
                new AcceptedCount(1L, 2L),
                new AcceptedCount(2L, 0L)
        );
        given(applicationRepository.countAcceptedByStudyPostIds(List.of(1L, 2L), ApplicationStatus.ACCEPTED))
                .willReturn(acceptedCounts);

        // when
        Page<StudyListResponse> result = studyService.findAll(null, null, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);

        StudyListResponse res1 = result.getContent().get(0);
        assertThat(res1.id()).isEqualTo(1L);
        assertThat(res1.title()).isEqualTo("자바 스터디 모집");
        assertThat(res1.writerNickname()).isEqualTo("홍길동");
        assertThat(res1.capacity()).isEqualTo(5);
        assertThat(res1.acceptedCount()).isEqualTo(2L);
        assertThat(res1.status()).isEqualTo("RECRUITING");

        StudyListResponse res2 = result.getContent().get(1);
        assertThat(res2.id()).isEqualTo(2L);
        assertThat(res2.acceptedCount()).isEqualTo(0L);

        verify(studyPostRepository).search(null, null, pageable);
        verify(applicationRepository).countAcceptedByStudyPostIds(List.of(1L, 2L), ApplicationStatus.ACCEPTED);
    }

    @Test
    @DisplayName("TODO 11 · 검색어와 상태가 있으면 레포지토리 search에 전달한다")
    void findAll_WithKeywordAndStatus_PassesParametersToRepository() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Member writer = createMember(1L, "kim@example.com", "김철수");
        StudyPost post = createPost(3L, "알고리즘 스터디", 4, LocalDate.of(2026, 9, 15), StudyStatus.CLOSED, writer);

        Page<StudyPost> postPage = new PageImpl<>(List.of(post), pageable, 1);
        given(studyPostRepository.search("알고리즘", StudyStatus.CLOSED, pageable)).willReturn(postPage);
        given(applicationRepository.countAcceptedByStudyPostIds(List.of(3L), ApplicationStatus.ACCEPTED))
                .willReturn(List.of(new AcceptedCount(3L, 4L)));

        // when
        Page<StudyListResponse> result = studyService.findAll("알고리즘", StudyStatus.CLOSED, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).status()).isEqualTo("CLOSED");
        assertThat(result.getContent().get(0).acceptedCount()).isEqualTo(4L);
        verify(studyPostRepository).search("알고리즘", StudyStatus.CLOSED, pageable);
    }

    @Test
    @DisplayName("TODO 11 · 검색어가 공백 문자열이면 null로 정규화하여 전달한다")
    void findAll_WithBlankKeyword_NormalizesToNull() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<StudyPost> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        given(studyPostRepository.search(null, StudyStatus.RECRUITING, pageable)).willReturn(emptyPage);

        // when
        Page<StudyListResponse> result = studyService.findAll("   ", StudyStatus.RECRUITING, pageable);

        // then
        assertThat(result.getContent()).isEmpty();
        verify(studyPostRepository).search(null, StudyStatus.RECRUITING, pageable);
    }

    @Test
    @DisplayName("TODO 11 · 조회 결과가 비어있으면 수락 카운트 쿼리를 호출하지 않고 빈 페이지를 반환한다")
    void findAll_EmptyResult_ReturnsEmptyPage() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<StudyPost> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        given(studyPostRepository.search(null, null, pageable)).willReturn(emptyPage);

        // when
        Page<StudyListResponse> result = studyService.findAll(null, null, pageable);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        verify(studyPostRepository).search(null, null, pageable);
    }
}
