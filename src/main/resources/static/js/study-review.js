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
                <div id="review-error"></div>
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
                        ${isMyReview ? `<button class="btn-delete" data-review-id="${r.id}">삭제</button>` : ''}
                    </li>
                `;
                });
                html += '</ul>';
            }

            $panel.innerHTML = html;
        } catch (err) {
            console.error(err);
        }


    /*
     */
    // TODO 58 · 후기 등록과 삭제

// 1. 후기 등록 함수
const writeBtn = document.querySelector('#btn-submit');

if (writeBtn) {
    writeBtn.addEventListener('click', async () => {
        const content = document.querySelector('#review-content')?.value;

        try {
            await api.post(`/api/studies/${StudyPage.studyId}/reviews`, {
                content: content
            });

            StudyPage.reload();

        } catch (error) {
            if (error.response && error.response.data) {
                const errData = error.response.data;

                if (errData.fieldErrors) {
                    showFieldErrors(errData.fieldErrors);
                } else {
                    showError(
                        '#review-error',
                        errData.message || '후기 등록 실패'
                    );
                }
            } else {
                showError(
                    '#review-error',
                    '오류가 발생했습니다.'
                );
            }
        }
    });
}


// 2. 후기 삭제 함수
const deleteBtns = document.querySelectorAll('.btn-delete');

deleteBtns.forEach(deleteBtn => {
    deleteBtn.addEventListener('click', async () => {

        const reviewId = deleteBtn.dataset.reviewId;

        if (!confirm('후기를 삭제하시겠습니까?')) {
            return;
        }

        try {
            await api.del(
                `/api/studies/${StudyPage.studyId}/reviews/${reviewId}`
            );

            StudyPage.reload();

        } catch (error) {
            if (error.response && error.response.data) {
                const errData = error.response.data;

                showError(
                    '#review-error',
                    errData.message || '후기 삭제 실패');
            } else {
                showError(
                    '#review-error',
                    '삭제 처리 중 오류가 발생했습니다.');
                 }
             }
         });
    });
});