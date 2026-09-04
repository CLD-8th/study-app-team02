# scripts

## push.sh

git 미숙련자용 push · PR 생성 스크립트.

**실행**

```bash
./scripts/push.sh
```

**확인 순서**

1. 브랜치명이 `todo/번호` 형식인지
2. push 대상 존재 여부
3. 금지 파일(`study.html`, `style.css`, `api.js`, `common.js`, `study-page.js`) 수정 여부
4. 커밋 메시지에 AI 관련 문구 포함 여부
5. 미커밋 변경사항 커밋(메시지 입력 요구)
6. 컴파일

실패 시 사유 출력 후 중단. 통과 시 push 진행 여부 확인 후 PR 생성까지 진행.

**최초 권한 설정**

```bash
chmod +x scripts/push.sh
```

## 더블클릭 실행

- 맥: `push.command`
- 윈도우: `push.bat` (Git Bash 필요)
