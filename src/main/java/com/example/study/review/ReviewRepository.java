package com.example.study.review;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 후기 목록.
     *
     * 작성자를 함께 가져와 목록 건수만큼 조회가 늘어나지 않게 함.
     */
    /*
     * TODO 51 · 후기 규약
     */
    @Query("select r from Review r join fetch r.writer where r.studyPost.id = :studyPostId order by r.id asc")
    List<Review> findByStudyPostIdOrderByIdAsc(@Param("studyPostId") Long studyPostId);

    boolean existsByWriterIdAndStudyPostId(Long writerId, Long studyPostId);
}

