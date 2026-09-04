/*
 * 후기 구획 · 담당 5
 *
 * 마감된 뒤에 참여자만 작성 가능하며 한 번 쓰면 입력란을 두지 않음.
 * 참여자는 모집자와 수락된 신청자를 가리킴.
 */

StudyPage.register(async function renderReviews() {
    /*
     * TODO 57 · 후기 목록과 입력란
     *
     * 기능        후기 목록을 조회해 그림 · 손님도 볼 수 있음
     *             입력란은 로그인 · 마감 · 참여자 · 미작성을 모두 만족할 때만 둠
     *             참여자는 모집자이거나 내 신청이 수락된 경우임
     *             자기 후기에만 삭제 단추를 둠
     * 활용메소드  StudyPage.isOwner() · StudyPage.myApplication   제공됨
     *             auth.memberId                                  api.js · 제공됨
     *             api.get() · dateTime() · escapeHtml()          제공됨
     *             GET /api/studies/{id}/reviews                  TODO 56 · 같은 담당
     * 받는자료    List<ReviewResponse> · writerId 로 내 후기를 가림
     * 그릴위치    SC-02 · #review-panel
     *             조각은 parts.html 의 "후기 입력" 과 "후기 항목"
     * 동작결과    모집 중에는 입력란이 없음 · 신청하지 않은 사람도 목록은 보임
     */

    /*
     * TODO 58 · 후기 등록과 삭제
     *
     * 기능        평점과 내용을 보내고 성공하면 다시 그림
     *             삭제는 확인을 받은 뒤 요청함
     *             항목별 사유가 오면 입력란 아래에 표시함
     * 활용메소드  api.post() · api.del()   api.js · 제공됨
     *             StudyPage.reload()       제공됨
     *             showFieldErrors() · showError()   common.js · 제공됨
     *             POST · DELETE 후기 주소   TODO 56 · 같은 담당
     * 받는자료    ReviewResponse · 실패는 ErrorResponse
     * 그릴위치    SC-02 · #review-error · #write-review · data-review
     * 동작결과    두 번째 작성은 400 DUPLICATE_REVIEW
     *             남의 후기에는 삭제 단추가 없음
     */
});
