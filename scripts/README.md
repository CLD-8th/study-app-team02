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

터미널 명령이 익숙하지 않으면 이 방법 사용.

**맥**

1. `study-app-team02` 폴더에서 `scripts` 폴더 진입
2. `push.command` 더블클릭
3. 최초 실행 시 "확인되지 않은 개발자" 경고가 뜨면 `push.command` 우클릭 → 열기 (한 번만 하면 됨)
4. 터미널 창이 열리고 자동 실행됨

**윈도우**

1. Git for Windows(Git Bash 포함) 설치 필요. 강의 기준 세팅이면 이미 설치돼 있음
2. `study-app-team02` 폴더에서 `scripts` 폴더 진입
3. `push.bat` 더블클릭
4. 명령 프롬프트 창이 열리고 자동 실행됨

두 경우 다 `push.sh`가 물어보는 질문(계속 진행 여부, 커밋 메시지, push 여부)에 답하면서 진행하면 됨.
