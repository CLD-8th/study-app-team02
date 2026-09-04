/*
 * 후기 구획 · 담당 5
 *
 * 마감된 뒤에 참여자만 작성 가능하며 한 번 쓰면 입력란을 두지 않음.
 * 참여자는 모집자와 수락된 신청자를 가리킴.
 */

StudyPage.register(async function renderReviews() {
    const box = document.getElementById('review-panel');
    const error = document.getElementById('page-error');
    const reviews = await api.get('/api/studies/' + StudyPage.id + '/reviews');

    const myId = auth.memberId;
    const alreadyWritten = reviews.some(function (r) { return r.writerId === myId; });

    const application = StudyPage.myApplication;
    const participant = StudyPage.isOwner() || (application && application.status === 'ACCEPTED');
    const closed = StudyPage.study.status !== 'RECRUITING';
    const canWrite = auth.loggedIn && closed && participant && !alreadyWritten;

    const items = reviews.map(function (r) {
        const stars = '★★★★★'.slice(0, r.rating);
        const mine = r.writerId === myId;
        return (
            '<div class="item">' +
            '<div><div class="item-title">' + escapeHtml(r.writerNickname) +
            ' <span style="color:#4f6ef0;">' + stars + '</span></div>' +
            '<div class="item-meta"><span>' + escapeHtml(r.content) + '</span></div></div>' +
            '<div class="item-meta"><span>' + dateTime(r.createdAt) + '</span>' +
            (mine ? '<button class="danger" data-id="' + r.id + '">삭제</button>' : '') +
            '</div></div>'
        );
    }).join('');

    const form = canWrite
        ? '<div class="card" style="background:#fafbfc; margin-bottom:14px;">' +
          '<div class="field" style="display:flex; gap:10px; align-items:center;">' +
          '<label>평점</label>' +
          '<select id="review-rating" style="width:90px;">' +
          '<option>5</option><option>4</option><option>3</option><option>2</option><option>1</option>' +
          '</select></div>' +
          '<div class="field">' +
          '<textarea id="review-content" placeholder="후기를 남겨 주세요"></textarea>' +
          '<div class="field-error hidden" id="review-content-error"></div>' +
          '</div>' +
          '<div class="actions"><button class="primary" id="review-submit">등록</button></div>' +
          '</div>'
        : '';

    box.innerHTML = '<div class="card-head"><div class="card-title">후기</div></div>' + form + items;
    box.classList.remove('hidden');

    if (canWrite) {
        document.getElementById('review-submit').addEventListener('click', async function () {
            const content = document.getElementById('review-content').value;
            const rating = Number(document.getElementById('review-rating').value);
            try {
                await api.post('/api/studies/' + StudyPage.id + '/reviews', { content: content, rating: rating });
                // 후기가 생겼으므로 다시 그려 입력란을 없애고 목록에 반영함.
                await StudyPage.reload();
            } catch (e) {
                if (!showFieldErrors(e, 'review-')) {
                    showError(error, e);
                }
            }
        });
    }

    box.querySelectorAll('button[data-id]').forEach(function (button) {
        button.addEventListener('click', async function () {
            if (!confirm('후기를 삭제할까요?')) return;
            try {
                await api.del('/api/reviews/' + button.dataset.id);
                await StudyPage.reload();
            } catch (e) {
                showError(error, e);
            }
        });
    });
});
