/*
 * 모집글 상세 구획 · 담당 2
 *
 * StudyPage.study 를 읽어 표시함. 자료를 직접 조회하지 않음.
 * 이 구획이 그려져야 담당 3 · 4 · 5 가 모집자 여부를 판단할 수 있음.
 */

StudyPage.register(async function renderDetail() {
    /*
     * TODO 27 · 상세 표시
     *
     * 기능        제목 · 상태 · 모집자 · 인원 · 마감일 · 소개를 그림
     *             모집자 본인일 때만 단추를 둠
     *             마감된 모집글은 수정과 마감 단추를 숨기고 삭제만 둠
     * 활용메소드  StudyPage.study · StudyPage.isOwner()   study-page.js · 제공됨
     *             badge() · shortDate() · dateTime()      common.js · 제공됨
     *             escapeHtml()                            common.js · 제공됨
     * 받는자료    StudyDetailResponse · TODO.md 응답 형태 참고
     * 그릴위치    SC-02 · #study-detail
     *             조각은 parts.html 의 "상세 머리"
     * 동작결과    남의 글에서는 단추가 보이지 않음
     */

    /*
     * TODO 28 · 단추 동작
     *
     * 기능        수정은 /form.html?id= 로 이동
     *             삭제와 마감은 확인을 받은 뒤 요청하며 마감 후에는 다시 그림
     *             삭제 후에는 목록으로 이동
     * 활용메소드  api.del() · api.patch()   api.js · 제공됨
     *             StudyPage.reload()        study-page.js · 제공됨 · 네 구획을 다시 그림
     *             confirm()                 확인 창
     *             DELETE · PATCH /api/studies   TODO 26 · 같은 담당
     * 받는자료    마감은 StudyDetailResponse · 삭제는 없음
     * 그릴위치    SC-02 · #edit · #remove · #close
     * 동작결과    마감을 누르면 배지가 마감으로 바뀌고 신청 구획이 사라짐
     */
});
