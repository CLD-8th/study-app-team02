#!/bin/bash
# 브랜치 push + PR 생성을 안전하게 대신 해주는 스크립트.
# 씀:  ./scripts/push.sh
# git과 gh(GitHub CLI)가 설치·로그인되어 있어야 함.

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

fail() {
    echo -e "${RED}✗ $1${NC}"
    exit 1
}

ok() {
    echo -e "${GREEN}✓ $1${NC}"
}

git rev-parse --is-inside-work-tree > /dev/null 2>&1 || fail "git 저장소 안에서 실행하기"

echo "== 0. 원격 최신화 =="
git fetch origin main -q || fail "origin/main 을 못 받아옴, 네트워크 확인하기"
ok "origin/main 확인함"

echo "== 1. 브랜치 확인 =="
BRANCH=$(git branch --show-current)

if [ "$BRANCH" = "main" ]; then
    fail "main 브랜치에서는 push 못 함. git switch -c todo/번호 로 브랜치부터 만들기"
fi

if ! [[ "$BRANCH" =~ ^todo/[0-9]+(-[0-9]+)?$ ]]; then
    echo -e "${YELLOW}! 브랜치 이름이 todo/번호 형식이 아님: $BRANCH${NC}"
    read -p "그래도 계속할까? (y/n) " CONTINUE
    [ "$CONTINUE" = "y" ] || exit 0
fi
ok "브랜치: $BRANCH"

echo "== 2. 변경 사항 확인 =="
UNPUSHED=$(git log origin/main..HEAD --oneline 2>/dev/null || true)
if [ -z "$(git status --porcelain)" ] && [ -z "$UNPUSHED" ]; then
    fail "push할 변경 사항이 없음"
fi

echo "== 3. 안 고칠 파일 확인 =="
CHANGED=$( { git diff --name-only origin/main...HEAD 2>/dev/null; git diff --name-only --cached; git diff --name-only; } || true)
BLOCKED_PATTERN='^src/main/resources/static/(study\.html|css/style\.css|js/(common|api|study-page)\.js)$'
BLOCKED=$(echo "$CHANGED" | sort -u | grep -E "$BLOCKED_PATTERN" || true)
if [ -n "$BLOCKED" ]; then
    fail "안 고치기로 한 파일이 바뀜:
$BLOCKED"
fi
ok "안 고칠 파일 건드리지 않음"

echo "== 4. 커밋 메시지 확인 =="
AI_PATTERN='claude|anthropic|co-authored-by'
EXISTING_MSGS=$(git log origin/main..HEAD --format=%B 2>/dev/null || true)
if [ -n "$EXISTING_MSGS" ] && echo "$EXISTING_MSGS" | grep -iE "$AI_PATTERN" > /dev/null; then
    fail "커밋 메시지에 AI 도구 관련 문구가 남아있음, 확인 후 다시 커밋하기"
fi
ok "커밋 메시지 확인됨"

echo "== 5. 커밋되지 않은 변경이 있으면 지금 커밋 =="
if [ -n "$(git status --porcelain)" ]; then
    git status --short
    read -p "커밋 메시지를 입력 (예: feat: 신청 목록 조회): " MSG
    [ -n "$MSG" ] || fail "커밋 메시지가 비어있음"
    if echo "$MSG" | grep -iE "$AI_PATTERN" > /dev/null; then
        fail "커밋 메시지에 AI 도구 관련 문구가 있음"
    fi
    git add -A
    git commit -m "$MSG"
    ok "커밋 완료"
fi

echo "== 6. 컴파일 확인 =="
if command -v gradle > /dev/null; then
    gradle compileJava --no-daemon -q || fail "컴파일 실패, 코드를 확인하기"
    ok "컴파일 통과"
else
    echo -e "${YELLOW}! gradle 명령을 못 찾아서 컴파일 확인은 건너뜀${NC}"
fi

echo ""
echo "여기까지 문제없음. 다음을 실행함:"
echo "  git push -u origin $BRANCH"
echo "  이후 PR 생성 화면으로 이동"
read -p "진행할까? (y/n) " CONFIRM
[ "$CONFIRM" = "y" ] || exit 0

git push -u origin "$BRANCH"
ok "push 완료"

if command -v gh > /dev/null && gh auth status > /dev/null 2>&1; then
    echo "== PR 생성 =="
    gh pr create --base main --head "$BRANCH" --fill --web
else
    echo -e "${YELLOW}! gh 로그인이 안 되어 있어서 PR은 직접 만들어야 함.${NC}"
    echo "https://github.com/CLD-8th/study-app-team02/pull/new/$BRANCH"
fi
