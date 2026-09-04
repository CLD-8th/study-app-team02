/*
 * 모집글 등록 · 수정 · 담당 1
 *
 * SC-03 화면. 주소에 id 가 없으면 등록, 있으면 수정임.
 */

const formId = param('id');

function defaultDeadline() {
    const date = new Date();
    date.setDate(date.getDate() + 7);
    return date.toISOString().substring(0, 10);
}

async function initForm() {
    if (!requireLogin()) {
        return;
    }

    if (formId) {
        document.getElementById('page-title').textContent = '모집글 수정';
        try {
            const data = await api.get('/api/studies/' + formId);
            document.getElementById('title').value = data.title || '';
            document.getElementById('content').value = data.content || '';
            document.getElementById('capacity').value = data.capacity || '';
            document.getElementById('deadline').value = data.deadline || '';
        } catch (e) {
            showError(document.getElementById('save-error'), e);
        }
    } else {
        document.getElementById('deadline').value = defaultDeadline();
    }
}

async function saveForm() {
    const saveErrorEl = document.getElementById('save-error');
    saveErrorEl.classList.add('hidden');
    showFieldErrors({ fields: null }, '');

    const title = document.getElementById('title').value;
    const content = document.getElementById('content').value;
    const capacity = Number(document.getElementById('capacity').value);
    const deadline = document.getElementById('deadline').value;

    const body = { title, content, capacity, deadline };

    try {
        let result;
        if (formId) {
            result = await api.put('/api/studies/' + formId, body);
        } else {
            result = await api.post('/api/studies', body);
        }
        const targetId = (result && result.id) ? result.id : formId;
        location.href = '/study.html?id=' + targetId;
    } catch (e) {
        if (!showFieldErrors(e, '')) {
            showError(saveErrorEl, e);
        }
    }
}

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('save').addEventListener('click', saveForm);
    document.getElementById('cancel').addEventListener('click', () => history.back());
    initForm();
});
