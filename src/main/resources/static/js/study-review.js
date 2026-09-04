/*
 * 후기 구획 · 담당 5
 *
 * 마감된 뒤에 참여자만 작성 가능하며 한 번 쓰면 입력란을 두지 않음.
 * 참여자는 모집자와 수락된 신청자를 가리킴.
 */

StudyPage.register(async function renderReviews() {
    /*
     * TODO 57 · 후기 목록과 입력란
     */
    StudyPage.register(async function renderReviews() {
        const $panel = document.querySelector('#review-panel');
        if (!$panel) return;

        try {
            // 1. 후기 데이터 조회
            const reviews = await api.get(`/api/studies/${StudyPage.studyId}/reviews`);

            const memberId = auth.memberId;
            const myApp = StudyPage.myApplication;

            // 조건 판단 (참여자 여부 & 작성 여부)
            const isParticipant = StudyPage.isOwner() || myApp?.status === 'ACCEPTED';
            const hasWritten = memberId && reviews.some(r => r.writerId === memberId);

            // 입력란 표시 조건: 로그인 + 마감됨 + 참여자 + 미작성
            const canWrite = memberId && !StudyPage.study.isRecruiting && isParticipant && !hasWritten;

            // 2. 입력란 HTML
            let html = canWrite ? `
            <div class="review-form">
                <textarea id="review-content"></textarea>
                <button id="btn-submit">등록</button>
            </div>
        ` : '';

            // 3. 후기 목록 HTML
            if (reviews?.length) {
                html += '<ul class="review-list">';
                reviews.forEach(r => {
                    const isMyReview = memberId && r.writerId === memberId;
                    html += `
                    <li>
                        <span>${escapeHtml(r.writerName)}</span>
                        <span>${dateTime(r.createdAt)}</span>
                        <p>${escapeHtml(r.content)}</p>
                        ${isMyReview ? '<button class="btn-delete">삭제</button>' : ''}
                    </li>
                `;
                });
                html += '</ul>';
            }

            $panel.innerHTML = html;
        } catch (err) {
            console.error(err);
        }
    });

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
