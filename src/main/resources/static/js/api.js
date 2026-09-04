/*
 * 요청 도구.
 *
 * 실패 응답이 하나로 통일되어 있으므로 읽는 쪽도 한 가지 방식으로 처리함.
 */

const TOKEN_KEY = 'study.accessToken';
const MEMBER_KEY = 'study.memberId';
const NICKNAME_KEY = 'study.nickname';

const auth = {
    save(token, memberId, nickname) {
        sessionStorage.setItem(TOKEN_KEY, token);
        sessionStorage.setItem(MEMBER_KEY, memberId);
        sessionStorage.setItem(NICKNAME_KEY, nickname);
    },
    clear() {
        sessionStorage.removeItem(TOKEN_KEY);
        sessionStorage.removeItem(MEMBER_KEY);
        sessionStorage.removeItem(NICKNAME_KEY);
    },
    get token() { return sessionStorage.getItem(TOKEN_KEY); },
    get memberId() {
        const value = sessionStorage.getItem(MEMBER_KEY);
        return value === null ? null : Number(value);
    },
    get nickname() { return sessionStorage.getItem(NICKNAME_KEY); },
    get loggedIn() { return this.token !== null; }
};

/**
 * 실패 응답을 담는 오류.
 *
 * code 로 분기하고 fields 로 입력란별 사유를 표시함.
 */
class ApiError extends Error {
    constructor(body) {
        super(body.message || '요청에 실패했습니다');
        this.status = body.status;
        this.code = body.code;
        this.fields = body.fields;
    }
}

async function request(method, url, body) {
    const headers = {};
    if (body !== undefined) {
        headers['Content-Type'] = 'application/json';
    }
    if (auth.loggedIn) {
        headers['Authorization'] = 'Bearer ' + auth.token;
    }

    const response = await fetch(url, {
        method,
        headers,
        body: body === undefined ? undefined : JSON.stringify(body)
    });

    if (response.status === 204) {
        return null;
    }

    const text = await response.text();
    const data = text ? JSON.parse(text) : null;

    if (!response.ok) {
        throw new ApiError(data || { status: response.status, message: '요청에 실패했습니다' });
    }
    return data;
}

const api = {
    get: (url) => request('GET', url),
    post: (url, body) => request('POST', url, body),
    put: (url, body) => request('PUT', url, body),
    patch: (url, body) => request('PATCH', url, body),
    del: (url) => request('DELETE', url)
};
