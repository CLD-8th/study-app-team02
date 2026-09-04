/*
 * 신청 목록 구획 · 담당 4
 *
 * 모집자만 볼 수 있음. 이 구획은 자료를 직접 조회함.
 */

StudyPage.register(async function renderApplications() {
    if (!StudyPage.isOwner()) return;

    const box = document.getElementById('application-panel');
    const applications = await api.get('/api/studies/' + StudyPage.id + '/applications');

    if (applications.length === 0) {
        box.innerHTML =
            '<div class="card-head"><div class="card-title">신청 목록</div></div>' +
            '<div class="item-meta">아직 신청이 없습니다</div>';
        box.classList.remove('hidden');
        return;
    }

    const full = StudyPage.study.acceptedCount >= StudyPage.study.capacity;

    const items = applications.map(function (a) {
        const meta = '<div class="item-meta"><span>' + escapeHtml(a.message || '') + '</span></div>';
        const title = '<div class="item-title">' + escapeHtml(a.applicantNickname) + ' ' + badge(a.status) + '</div>';

        if (a.status === 'PENDING') {
            const actions =
                '<div class="actions">' +
                '<button class="primary" data-id="' + a.id + '" data-action="accept">수락</button>' +
                '<button data-id="' + a.id + '" data-action="reject">거절</button>' +
                '</div>';
            return '<div class="item"><div>' + title + meta + '</div>' + actions + '</div>';
        }

        return (
            '<div class="item"><div>' + title + meta + '</div>' +
            '<span class="item-meta">' + shortDate(a.createdAt) + '</span></div>'
        );
    }).join('');

    box.innerHTML =
        '<div class="card-head">' +
        '<div class="card-title">신청 목록</div>' +
        '<span class="card-count">' + applications.length + '건</span>' +
        '</div>' +
        items +
        (full ? '<div class="alert alert-error">정원이 찼습니다. 더 수락할 수 없습니다</div>' : '');

    box.classList.remove('hidden');

    box.querySelectorAll('button[data-action]').forEach(function (button) {
        button.addEventListener('click', function () {
            processApplication(button.dataset.id, button.dataset.action);
        });
    });
});

async function processApplication(applicationId, action) {
    /*
     * TODO 48 · 수락과 거절 처리
     *
     * 기능        수락과 거절은 주소의 끝만 다르므로 하나로 묶음
     *             성공하면 다시 그려 인원과 상태를 갱신함
     *             실패하면 사유를 안내에 표시함
     * 활용메소드  api.patch()          api.js · 제공됨
     *             StudyPage.reload()   제공됨 · 상세의 인원도 함께 바뀜
     *             showError()          common.js · 제공됨
     *             PATCH /api/applications/{id}/accept · reject   TODO 46 · 같은 담당
     * 받는자료    ApplicationResponse · 실패는 ErrorResponse
     * 그릴위치    SC-02 · #page-error
     * 동작결과    마지막 자리를 수락하면 상세의 배지가 마감으로 바뀜
     *             정원이 찬 뒤 수락하면 400 CAPACITY_EXCEEDED
     */
}
