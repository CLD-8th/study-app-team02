package com.example.study.review;

import com.example.study.review.dto.ReviewRequest;
import com.example.study.review.dto.ReviewResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /*
     * TODO 56 · 후기 주소 셋

     */
    // 후기 목록 조회 (손님도 볼 수 있음, 토큰 없음)
    @GetMapping("/api/studies/{studyId}/reviews")
    public ResponseEntity<List<ReviewResponse>> findByStudy(@PathVariable Long studyId) {
        List<ReviewResponse> response = reviewService.findByStudy(studyId);
        return ResponseEntity.ok(response);
    }


    // 후기 등록 (POST 201)
    @PostMapping("/api/studies/{studyId}/reviews")
    public ResponseEntity<ReviewResponse> create(
            @PathVariable Long studyId,
            @RequestBody ReviewSaveRequest request,
            @AuthenticationPrincipal AuthMember authMember) {
        ReviewResponse response = reviewService.create(
                studyId,
                request.content(),
                request.rating(),
                authMember.getId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    // 후기 삭제 (DELETE 204)
    @DeleteMapping("/api/studies/{studyId}/reviews/{reviewId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal AuthMember authMember) {
        reviewService.delete(reviewId, authMember.getId());
        return ResponseEntity.noContent().build();
    }
}
