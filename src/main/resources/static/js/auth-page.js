/*
 * 로그인과 가입 · 제공
 *
 * 담당이 없는 제공 화면임. 스켈레톤에서도 그대로 주어짐.
 */

function bindLogin() {
    const button = document.getElementById('login');
    if (!button) return;

    button.addEventListener('click', async () => {
        const box = document.getElementById('login-error');
        box.classList.add('hidden');
        document.querySelectorAll('input').forEach(node => node.classList.remove('invalid'));

        try {
            const token = await api.post('/api/auth/login', {
                email: document.getElementById('email').value.trim(),
                password: document.getElementById('password').value
            });
            auth.save(token.accessToken, token.memberId, token.nickname);
            location.href = '/index.html';
        } catch (error) {
            // 이메일이 없는 경우와 비밀번호가 틀린 경우를 구분해 알리지 않음.
            if (!showFieldErrors(error, '')) {
                document.getElementById('email').classList.add('invalid');
                document.getElementById('password').classList.add('invalid');
                box.textContent = '이메일 또는 비밀번호가 올바르지 않습니다';
                box.classList.remove('hidden');
            }
        }
    });

    document.getElementById('password').addEventListener('keyup', (event) => {
        if (event.key === 'Enter') button.click();
    });
}

function bindSignup() {
    const button = document.getElementById('signup');
    if (!button) return;

    button.addEventListener('click', async () => {
        const box = document.getElementById('signup-error');
        box.classList.add('hidden');
        showFieldErrors({ fields: null }, '');

        const password = document.getElementById('password').value;
        const confirm = document.getElementById('confirm').value;

        // 확인란은 화면에서만 쓰는 값임. 서버에 보내지 않음.
        if (password !== confirm) {
            document.getElementById('confirm').classList.add('invalid');
            const label = document.getElementById('confirm-error');
            label.textContent = '비밀번호가 일치하지 않습니다';
            label.classList.remove('hidden');
            return;
        }

        try {
            await api.post('/api/members', {
                email: document.getElementById('email').value.trim(),
                password: password,
                nickname: document.getElementById('nickname').value.trim()
            });
            alert('가입이 완료되었습니다');
            location.href = '/login.html';
        } catch (error) {
            if (!showFieldErrors(error, '')) {
                showError(box, error);
            }
        }
    });
}

document.addEventListener('DOMContentLoaded', () => {
    bindLogin();
    bindSignup();
});
