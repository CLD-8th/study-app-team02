/*
 * 화면 공통.
 *
 * 머리 영역 표시와 값 형식을 한곳에서 처리함.
 */

const STATUS_LABEL = {
    RECRUITING: '모집 중',
    CLOSED: '마감',
    PENDING: '대기',
    ACCEPTED: '수락됨',
    REJECTED: '거절됨',
    CANCELED: '취소함'
};

function badge(status) {
    return '<span class="badge badge-' + status + '">' + (STATUS_LABEL[status] || status) + '</span>';
}

/** 2026-09-30 을 09-30 으로 */
function shortDate(value) {
    return value ? String(value).substring(5, 10) : '';
}

/** 2026-09-03T10:15:30 을 09-03 10:15 로 */
function dateTime(value) {
    if (!value) return '';
    return String(value).substring(5, 10) + ' ' + String(value).substring(11, 16);
}

function escapeHtml(value) {
    return String(value ?? '')
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;');
}

/** 머리 영역. 로그인 전후에 보이는 것이 다름. */
function renderHeader() {
    const menu = document.getElementById('header-menu');
    if (!menu) return;

    if (auth.loggedIn) {
        menu.innerHTML =
            '<span class="nickname">' + escapeHtml(auth.nickname) + '</span>' +
            '<a href="/mypage.html">마이페이지</a>' +
            '<a href="#" id="logout">로그아웃</a>';
        document.getElementById('logout').addEventListener('click', async (event) => {
            event.preventDefault();
            try {
                await api.post('/api/auth/logout', {});
            } catch (e) {
                // 보관한 토큰을 지우는 것이 실제 효과이므로 실패해도 진행함.
            }
            auth.clear();
            location.href = '/index.html';
        });
    } else {
        menu.innerHTML =
            '<a href="/login.html">로그인</a>' +
            '<a href="/signup.html">가입</a>';
    }
}

/** 인증이 필요한 화면에서 호출. */
function requireLogin() {
    if (!auth.loggedIn) {
        location.href = '/login.html';
        return false;
    }
    return true;
}

function showError(element, error) {
    element.textContent = error.message;
    element.classList.remove('hidden');
}

/** 항목별 사유를 각 입력란 아래에 표시. */
function showFieldErrors(error, prefix) {
    document.querySelectorAll('.field-error').forEach(node => node.classList.add('hidden'));
    document.querySelectorAll('input, textarea').forEach(node => node.classList.remove('invalid'));

    if (!error.fields) return false;

    Object.entries(error.fields).forEach(([name, message]) => {
        const input = document.getElementById(prefix + name);
        const label = document.getElementById(prefix + name + '-error');
        if (input) input.classList.add('invalid');
        if (label) {
            label.textContent = message;
            label.classList.remove('hidden');
        }
    });
    return true;
}

function param(name) {
    return new URLSearchParams(location.search).get(name);
}

document.addEventListener('DOMContentLoaded', renderHeader);
