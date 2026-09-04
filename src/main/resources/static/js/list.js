/*
 * 모집글 목록 · 담당 1
 *
 * SC-01 모집글 목록 화면을 그림.
 */

let listPage = 0;

function renderList(data) {
    document.getElementById('total').textContent = (data.totalElements || 0) + '건';
    const listEl = document.getElementById('list');
    const pagerEl = document.getElementById('pager');

    if (!data.content || data.content.length === 0) {
        listEl.innerHTML = '<div class="empty">등록된 모집글이 없습니다</div>';
        pagerEl.innerHTML = '';
        return;
    }

    listEl.innerHTML = data.content.map(item => `
        <div class="item ${item.status === 'CLOSED' ? 'closed' : ''}">
            <div>
                <div class="item-title"><a href="/study.html?id=${item.id}">${escapeHtml(item.title)}</a></div>
                <div class="item-meta">
                    <span>${escapeHtml(item.writerNickname)}</span>
                    <span>${item.acceptedCount} / ${item.capacity}명</span>
                </div>
            </div>
            <div class="item-meta">
                ${badge(item.status)}
                <span>~ ${shortDate(item.deadline)}</span>
            </div>
        </div>
    `).join('');

    renderPager(data);
}

function renderPager(data) {
    const pagerEl = document.getElementById('pager');
    pagerEl.innerHTML = '';

    if (!data.totalPages || data.totalPages <= 0) {
        return;
    }

    for (let i = 0; i < data.totalPages; i++) {
        const btn = document.createElement('button');
        btn.textContent = String(i + 1);
        if (i === data.page) {
            btn.classList.add('current');
        }
        btn.addEventListener('click', () => {
            listPage = i;
            loadList();
        });
        pagerEl.appendChild(btn);
    }
}

async function loadList() {
    const keyword = document.getElementById('keyword').value.trim();
    const status = document.getElementById('status').value;
    const errorEl = document.getElementById('load-error');
    errorEl.classList.add('hidden');

    const params = new URLSearchParams();
    params.set('page', listPage);
    params.set('size', 10);
    if (keyword) {
        params.set('keyword', keyword);
    }
    if (status) {
        params.set('status', status);
    }

    try {
        const data = await api.get('/api/studies?' + params.toString());
        renderList(data);
    } catch (e) {
        errorEl.classList.remove('hidden');
        document.getElementById('list').innerHTML = '';
        document.getElementById('pager').innerHTML = '';
        document.getElementById('total').textContent = '0건';
    }
}

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('search').addEventListener('click', () => { listPage = 0; loadList(); });
    document.getElementById('retry').addEventListener('click', loadList);
    document.getElementById('create').addEventListener('click', () => location.href = '/form.html');

    // 등록 단추는 로그인한 경우에만 보임.
    if (auth.loggedIn) {
        document.getElementById('create').classList.remove('hidden');
    }
    loadList();
});
