package com.example.study.application;

import com.example.study.member.Member;
import com.example.study.study.StudyPost;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 신청.
 *
 * 모집글과 신청자 조합에 유일 제약을 두지 않음.
 * 재신청을 허용하는 팀에서는 같은 회원의 신청이 둘 이상 존재하며,
 * 제약이 있으면 저장 자체가 실패함. 중복은 업무 계층에서 확인함.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_post_id", nullable = false)
    private StudyPost studyPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private Member applicant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApplicationStatus status;

    @Column(length = 300)
    private String message;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ── 예비 열
    private LocalDateTime processedAt;

    @Column(length = 200)
    private String rejectReason;

    public Application(StudyPost studyPost, Member applicant, String message) {
        this.studyPost = studyPost;
        this.applicant = applicant;
        this.message = message;
        this.status = ApplicationStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void accept() {
        this.status = ApplicationStatus.ACCEPTED;
        this.processedAt = LocalDateTime.now();
    }

    public void reject() {
        this.status = ApplicationStatus.REJECTED;
        this.processedAt = LocalDateTime.now();
    }

    public boolean isPending() {
        return this.status == ApplicationStatus.PENDING;
    }

    public boolean isAppliedBy(Long memberId) {
        return this.applicant.getId().equals(memberId);
    }
}
