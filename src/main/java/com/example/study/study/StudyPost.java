package com.example.study.study;

import com.example.study.member.Member;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 모집글.
 *
 * 정원과 마감일을 가지며 상태는 모집 중에서 마감으로만 전이함.
 * 마감일이 지나도 상태는 그대로 두며 신청 시점에 날짜를 함께 확인함.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StudyStatus status;

    // 모집자. 다대일의 기본 조회 시점은 함께 조회이므로 지연을 명시함.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private Member writer;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // ── 예비 열
    @Column(nullable = false)
    private int viewCount;

    @Column(length = 30)
    private String category;

    private LocalDate startDate;

    @Column(length = 20)
    private String meetingType;

    @Column(length = 100)
    private String location;

    public StudyPost(String title, String content, int capacity, LocalDate deadline, Member writer) {
        this.title = title;
        this.content = content;
        this.capacity = capacity;
        this.deadline = deadline;
        this.writer = writer;
        this.status = StudyStatus.RECRUITING;
        this.createdAt = LocalDateTime.now();
        this.viewCount = 0;
    }

    public void update(String title, String content, int capacity, LocalDate deadline) {
        this.title = title;
        this.content = content;
        this.capacity = capacity;
        this.deadline = deadline;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 모집 마감.
     *
     * 모집자가 직접 마감하거나 정원의 마지막 자리가 채워질 때 호출됨.
     */
    public void close() {
        this.status = StudyStatus.CLOSED;
    }

    public boolean isWrittenBy(Long memberId) {
        return this.writer.getId().equals(memberId);
    }

    public boolean isRecruiting() {
        return this.status == StudyStatus.RECRUITING;
    }

    public boolean isDeadlinePassed() {
        return this.deadline.isBefore(LocalDate.now());
    }
}
