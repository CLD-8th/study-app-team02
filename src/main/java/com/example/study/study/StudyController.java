package com.example.study.study;

import com.example.study.common.PageResponse;
import com.example.study.study.dto.StudyDetailResponse;
import com.example.study.study.dto.StudyListResponse;
import com.example.study.study.dto.StudyRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.function.Function;

/**
 * 모집글 표현 계층.
 *
 * 예외를 잡지 않음. 전역 처리기가 받아 같은 형태로 변환함.
 * 모집자는 요청 본문이 아니라 토큰에서 확인함.
 */
@RestController
@RequestMapping("/api/studies")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    @GetMapping
    public PageResponse<StudyListResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) StudyStatus status) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<StudyListResponse> result = studyService.findAll(keyword, status, pageRequest);
        return PageResponse.of(result, Function.identity());
    }

    /**
     * 모집글 상세를 조회함. 손님도 볼 수 있어 인증을 요구하지 않음. EP-02.
     *
     * @param id 모집글 식별자
     * @return 모집글 상세, 200
     */
    @GetMapping("/{id}")
    public StudyDetailResponse findOne(@PathVariable Long id) {
        return studyService.findById(id);
    }

    /**
     * 모집글을 등록함. 모집자를 본문으로 받지 않고 토큰에서 확인함. 받으면 위조가 가능함. EP-03.
     *
     * @param request 제목 · 내용 · 정원 · 마감일
     * @param memberId 토큰에서 확인한 회원 식별자, 모집자가 됨
     * @return 등록된 모집글, 201과 Location 머리
     */
    @PostMapping
    public ResponseEntity<StudyDetailResponse> create(@Valid @RequestBody StudyRequest request,
                                                      @AuthenticationPrincipal Long memberId) {
        StudyDetailResponse created = studyService.create(
                request.title(), request.content(), request.capacity(), request.deadline(), memberId);

        return ResponseEntity.created(URI.create("/api/studies/" + created.id())).body(created);
    }

    /**
     * 모집글을 수정함. EP-04.
     *
     * @param id 모집글 식별자
     * @param request 제목 · 내용 · 정원 · 마감일
     * @param memberId 토큰에서 확인한 회원 식별자
     * @return 수정된 모집글, 200
     */
    @PutMapping("/{id}")
    public StudyDetailResponse update(@PathVariable Long id,
                                      @Valid @RequestBody StudyRequest request,
                                      @AuthenticationPrincipal Long memberId) {
        return studyService.update(
                id, request.title(), request.content(), request.capacity(), request.deadline(), memberId);
    }

    /**
     * 모집글을 삭제함. 돌려줄 내용이 없으므로 204 로 응답함. EP-05.
     *
     * @param id 모집글 식별자
     * @param memberId 토큰에서 확인한 회원 식별자
     * @return 204
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal Long memberId) {
        studyService.delete(id, memberId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 모집글을 마감함. 본문이 없음, 주소만으로 무엇을 할지 결정함. EP-06.
     *
     * @param id 모집글 식별자
     * @param memberId 토큰에서 확인한 회원 식별자
     * @return 마감된 모집글, 200
     */
    @PatchMapping("/{id}/close")
    public StudyDetailResponse close(@PathVariable Long id,
                                     @AuthenticationPrincipal Long memberId) {
        return studyService.close(id, memberId);
    }
}
