/*
 * 모집글 상세 구획 · 담당 2
 *
 * StudyPage.study 를 읽어 표시함. 자료를 직접 조회하지 않음.
 * 이 구획이 그려져야 담당 3 · 4 · 5 가 모집자 여부를 판단할 수 있음.
 */

StudyPage.register(async function renderDetail() {
    const study = StudyPage.study;
    const box = document.getElementById('study-detail');
    const owner = StudyPage.isOwner();
    const recruiting = study.status === 'RECRUITING';

    // 모집자 본인일 때만 단추를 둠.
    // 마감된 뒤에는 수정과 마감이 의미가 없으므로 삭제만 남김.
    let actions = '';
    if (owner) {
        actions =
            '<div class="actions">' +
            (recruiting ? '<button id="edit">수정</button>' : '') +
            '<button class="danger" id="remove">삭제</button>' +
            (recruiting ? '<button class="primary" id="close">모집 마감</button>' : '') +
            '</div>';
    }

    box.innerHTML =
        '<div class="card-head">' +
        '<div class="card-title" style="font-size:19px;">' + escapeHtml(study.title) + '</div>' +
        badge(study.status) +
        '</div>' +
        '<div class="item-meta" style="margin-bottom:12px;">' +
        '<span>' + escapeHtml(study.writerNickname) + '</span>' +
        '<span>' + study.acceptedCount + ' / ' + study.capacity + '명</span>' +
        '<span>~ ' + shortDate(study.deadline) + '</span>' +
        '<span>' + dateTime(study.createdAt) + '</span>' +
        '</div>' +
        '<div style="font-size:13px; line-height:1.7; white-space:pre-wrap;">' +
        escapeHtml(study.content) +
        '</div>' +
        actions;

    box.classList.remove('hidden');

    if (!owner) return;

    const error = document.getElementById('page-error');

    if (recruiting) {
        document.getElementById('edit').addEventListener('click', () => {
            location.href = '/form.html?id=' + StudyPage.id;
        });

        document.getElementById('close').addEventListener('click', async () => {
            if (!confirm('모집을 마감할까요? 되돌릴 수 없습니다.')) return;
            try {
                await api.patch('/api/studies/' + StudyPage.id + '/close');
                // 상태가 바뀌었으므로 네 구획을 함께 다시 그림.
                await StudyPage.reload();
            } catch (e) {
                showError(error, e);
            }
        });
    }

    document.getElementById('remove').addEventListener('click', async () => {
        if (!confirm('모집글을 삭제할까요? 되돌릴 수 없습니다.')) return;
        try {
            await api.del('/api/studies/' + StudyPage.id);
            location.href = '/index.html';
        } catch (e) {
            showError(error, e);
        }
    });
});
