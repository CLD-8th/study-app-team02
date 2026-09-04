#!/bin/bash
# 더블클릭으로 실행하는 용도. push.sh 를 대신 부름.
cd "$(dirname "$0")/.." || exit 1
./scripts/push.sh
echo ""
read -p "완료. 엔터 입력 시 종료: " _
