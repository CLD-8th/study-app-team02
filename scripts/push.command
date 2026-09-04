#!/bin/bash
# 더블클릭으로 실행하는 용도. push.sh 를 대신 부름.
cd "$(dirname "$0")/.." || exit 1
./scripts/push.sh
echo ""
read -p "끝났음, 엔터 누르면 창 닫힘 " _
