# API 테스트 하는 법

Postman 없이 터미널 curl 만으로 자기 TODO를 확인하는 법. 순서대로 따라하면 됨.

## 0. 준비

앱이 기동돼 있어야 함(`localhost:8090`). 샘플 자료(README "샘플 자료" 절 참고)가 들어있어야 아래 예시가 그대로 됨.

**샘플 계정** (전부 비밀번호 `1234`)

| 이메일 | 별명 | 비고 |
| --- | --- | --- |
| hong@example.com | 홍길동 | 모집글 1·3의 모집자 |
| kim@example.com | 김철수 | |
| lee@example.com | 이영희 | |
| park@example.com | 박민수 | |
| choi@example.com | 최지은 | |

## 1. 로그인해서 토큰 받기

```bash
curl -s -X POST http://localhost:8090/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"hong@example.com","password":"1234"}'
```

응답에서 `accessToken` 값을 복사함. 매번 복사하기 귀찮으면 변수에 담아둠.

```bash
TOKEN=$(curl -s -X POST http://localhost:8090/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"hong@example.com","password":"1234"}' \
  | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)

echo $TOKEN
```

`echo $TOKEN` 쳤을 때 긴 문자열이 나오면 성공.

## 2. 요청 보내기

**GET (조회, 토큰 필요 없는 것도 있음)**

```bash
curl -s http://localhost:8090/api/studies
```

**GET (조회, 토큰 필요한 것)**

```bash
curl -s http://localhost:8090/api/studies/1/applications \
  -H "Authorization: Bearer $TOKEN"
```

**POST (등록)**

```bash
curl -s -X POST http://localhost:8090/api/studies \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"테스트 모집글","content":"내용","capacity":5,"deadline":"2026-12-31"}'
```

**PATCH (상태 전이, 본문 없는 경우)**

```bash
curl -s -X PATCH http://localhost:8090/api/studies/1/close \
  -H "Authorization: Bearer $TOKEN"
```

**DELETE**

```bash
curl -s -X DELETE http://localhost:8090/api/studies/1 \
  -H "Authorization: Bearer $TOKEN"
```

## 3. 응답 코드까지 같이 보고 싶을 때

`-i` 옵션 붙이면 응답 코드(200, 403 등)도 같이 나옴.

```bash
curl -si http://localhost:8090/api/studies/9999 | head -1
```

## 4. 담당별 확인 예시

담당 번호는 `04_기능명세서.md` 기준.

### 담당 3 · 신청 (TODO 31~35)

```bash
# hong 으로 로그인한 토큰 필요 없음 - 김철수(kim)가 남의 글에 신청하는 상황
KIM_TOKEN=$(curl -s -X POST http://localhost:8090/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"kim@example.com","password":"1234"}' \
  | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)

curl -si -X POST http://localhost:8090/api/studies/2/applications \
  -H "Authorization: Bearer $KIM_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"message":"참여하고 싶습니다"}'
```

### 담당 4 · 신청 목록·수락·거절 (TODO 41~48)

```bash
# hong(모집자)이 모집글 1의 신청 목록 확인
curl -s http://localhost:8090/api/studies/1/applications \
  -H "Authorization: Bearer $TOKEN"

# 신청 3번 수락
curl -si -X PATCH http://localhost:8090/api/applications/3/accept \
  -H "Authorization: Bearer $TOKEN"
```

### 담당 5 · 후기 (TODO 51~58)

```bash
# 마감된 모집글(sample.sql 기준 3번)에 후기 등록
curl -si -X POST http://localhost:8090/api/studies/3/reviews \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"content":"좋은 스터디였습니다","rating":5}'
```

### 담당 6 · 마이페이지 (TODO 61~68)

```bash
curl -s http://localhost:8090/api/members/me \
  -H "Authorization: Bearer $TOKEN"

curl -s http://localhost:8090/api/members/me/studies \
  -H "Authorization: Bearer $TOKEN"
```

## 5. 실패 케이스 확인 (권한·상태 판단 검증용)

| 상황 | 명령 | 기대 코드 |
| --- | --- | --- |
| 토큰 없이 요청 | `curl -si http://localhost:8090/api/studies/1/applications` | 401 |
| 남의 글 조회(모집자 아님) | 위 KIM_TOKEN 으로 `/api/studies/1/applications` 요청 | 403 |
| 없는 모집글 조회 | `curl -si http://localhost:8090/api/studies/9999` | 404 |

응답 코드가 예상과 다르면 `docs/api.md`, `docs/functions.md` 다시 확인.
