package com.example.study.review;

import com.example.study.member.Member;
import com.example.study.study.StudyPost;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * 후기.
 *
 * 평점 범위는 요청 형태에서 확인하며 표 제약으로 두지 않음.
 * 유일 제약도 두지 않음. 여럿 남기도록 허용하는 팀이 있음.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false)
    private int rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_post_id", nullable = false)
    private StudyPost studyPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private Member writer;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ── 예비 열
    private LocalDateTime updatedAt;

    public Review(String content, int rating, StudyPost studyPost, Member writer) {
        this.content = content;
        this.rating = rating;
        this.studyPost = studyPost;
        this.writer = writer;
        this.createdAt = LocalDateTime.now();
    }

    public boolean isWrittenBy(Long memberId) {
        return this.writer.getId().equals(memberId);
    }
}
