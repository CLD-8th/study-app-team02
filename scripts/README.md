# scripts

## push.sh

브랜치 push · PR 생성을 대신 해줌. git 명령이 헷갈리는 사람용.

```bash
./scripts/push.sh
```

todo 브랜치에서 작업 다 끝내고 실행하면 됨. 순서대로 확인함.

1. 브랜치 이름이 todo/번호 인지
2. push할 게 있는지
3. 안 고치기로 한 파일(study.html, style.css, api.js, common.js, study-page.js) 건드렸는지
4. 커밋 메시지에 이상한 문구 없는지
5. 커밋 안 했으면 메시지 물어보고 커밋
6. 컴파일 되는지

하나라도 걸리면 멈추고 이유를 알려줌. 다 통과하면 push할지 물어보고, 하면 PR 화면까지 열어줌.

첫 실행 전에 실행 권한 필요하면:

```bash
chmod +x scripts/push.sh
```

## 더블클릭으로 실행

터미널이 아직 낯설면 이거.

- 맥: `push.command` 더블클릭
- 윈도우: `push.bat` 더블클릭 (Git Bash 설치돼 있어야 함, 강의 기준 세팅이면 이미 있음)
