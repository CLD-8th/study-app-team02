# study-app API 명세

| 항목 | 값 |
| --- | --- |
| 프로젝트 | 스터디 모집 |
| 기본 주소 | `http://localhost:8090` |
| 버전 | v1.0.0 |

**식별자.**

| 접두 | 대상 | 낱말 |
| --- | --- | --- |
| `FR` | 기능 요구사항 | Functional Requirement |
| `NFR` | 비기능 요구사항 | Non-Functional Requirement |
| `BR` | 업무 규칙 | Business Rule |
| `SC` | 화면 | Screen |
| `EP` | 주소 | Endpoint |

**주석의 `동작결과` 줄에 적힌 `EP-NN` 을 여기서 찾음.**

---

## 01. 공통 사항

### 요청 머리

| 이름 | 값 | 필요 시점 |
| --- | --- | --- |
| `Content-Type` | `application/json` | 본문이 있을 때 |
| `Authorization` | `Bearer <토큰>` | 인증이 필요할 때 |

### 값 위치 규칙

| 방식 | 값 위치 | 수신 |
| --- | --- | --- |
| GET | 질의 | `@RequestParam` |
| POST · PUT · PATCH | 본문 | `@RequestBody` |
| 자원 식별 | 경로 | `@PathVariable` |

### 공통 실패 응답

모든 실패는 같은 형태로 반환한다.

```json
{
  "status": 400,
  "code": "DUPLICATE_APPLICATION",
  "message": "이미 신청한 모집글",
  "fields": null,
  "timestamp": "2026-09-04T10:15:30"
}
```

| 항목 | 설명 |
| --- | --- |
| `status` | 응답 코드 |
| `code` | 화면이 분기할 기준 |
| `message` | 사용자에게 표시할 문구 |
| `fields` | 항목별 실패 사유 |
| `timestamp` | 발생 시각 |

### 실패 코드 목록

| 코드 | 응답 | 사유 | 구분 |
| --- | --- | --- | --- |
| `INVALID_INPUT` | 400 | 검증 실패 | 기준 |
| `DUPLICATE_APPLICATION` | 400 | 이미 신청함 | 기준 |
| `STUDY_CLOSED` | 400 | 마감된 모집글 | 기준 |
| `DEADLINE_PASSED` | 400 | 마감일이 지남 | 기준 |
| `SELF_APPLICATION` | 400 | 자기 모집글 | 기준 |
| `CAPACITY_EXCEEDED` | 400 | 정원이 참 | 기준 |
| `CAPACITY_BELOW_ACCEPTED` | 400 | 정원이 수락 인원보다 작음 | 기준 |
| `ALREADY_PROCESSED` | 400 | 이미 처리된 신청 | 기준 |
| `STUDY_NOT_CLOSED` | 400 | 마감되지 않은 스터디 | 기준 |
| `DUPLICATE_REVIEW` | 400 | 이미 작성한 후기 | 팀 판단 |
| `UNAUTHORIZED` | 401 | 인증 부재 | 기준 |
| `FORBIDDEN` | 403 | 권한 부족 | 기준 |
| `NOT_FOUND` | 404 | 대상 부재 | 기준 |
| `INTERNAL_ERROR` | 500 | 처리하지 못한 문제 | 기준 |

**`STUDY_CLOSED` 와 `DEADLINE_PASSED` 를 나눈다.** 상태가 마감인 경우와 마감일이 지난 경우는 사유가 다르므로 화면 안내도 달라야 한다. 둘 다 해당하면 상태를 먼저 판단한다.

**`DUPLICATE_REVIEW` 는 BR-14 를 채택한 팀만 쓴다.** 채택하지 않으면 목록에서 지운다.

### 목록 응답 형태

쪽 단위로 반환하는 경우 아래 형태를 사용한다.

```json
{
  "content": [],
  "page": 0,
  "size": 10,
  "totalElements": 0,
  "totalPages": 0,
  "first": true,
  "last": true
}
```

---

## 02. 주소 목록

### 모집글

| 번호 | 방식 | 주소 | 기능 | 인증 | 담당 |
| --- | --- | --- | --- | --- | --- |
| EP-01 | GET | `/api/studies` | 목록 조회 | — | 1 |
| EP-02 | GET | `/api/studies/{id}` | 상세 조회 | — | 2 |
| EP-03 | POST | `/api/studies` | 등록 | 예 | 2 |
| EP-04 | PUT | `/api/studies/{id}` | 수정 | 예 | 2 |
| EP-05 | DELETE | `/api/studies/{id}` | 삭제 | 예 | 2 |
| EP-06 | PATCH | `/api/studies/{id}/close` | 마감 | 예 | 2 |

### 신청

| 번호 | 방식 | 주소 | 기능 | 인증 | 담당 |
| --- | --- | --- | --- | --- | --- |
| EP-07 | POST | `/api/studies/{studyId}/applications` | 신청 | 예 | 3 |
| EP-08 | DELETE | `/api/applications/{id}` | 취소 | 예 | 3 |
| EP-09 | GET | `/api/studies/{studyId}/applications` | 목록 조회 | 예 | 4 |
| EP-10 | PATCH | `/api/applications/{id}/accept` | 수락 | 예 | 4 |
| EP-11 | PATCH | `/api/applications/{id}/reject` | 거절 | 예 | 4 |

### 후기

| 번호 | 방식 | 주소 | 기능 | 인증 | 담당 |
| --- | --- | --- | --- | --- | --- |
| EP-12 | GET | `/api/studies/{studyId}/reviews` | 목록 조회 | — | 5 |
| EP-13 | POST | `/api/studies/{studyId}/reviews` | 등록 | 예 | 5 |
| EP-14 | DELETE | `/api/reviews/{id}` | 삭제 | 예 | 5 |

### 마이페이지

| 번호 | 방식 | 주소 | 기능 | 인증 | 담당 |
| --- | --- | --- | --- | --- | --- |
| EP-15 | GET | `/api/members/me` | 내 정보 | 예 | 6 |
| EP-16 | GET | `/api/members/me/studies` | 내 모집글 | 예 | 6 |
| EP-17 | GET | `/api/members/me/applications` | 내 신청 | 예 | 6 |

### 회원과 인증 · 제공

| 번호 | 방식 | 주소 | 기능 | 인증 |
| --- | --- | --- | --- | --- |
| EP-18 | POST | `/api/members` | 가입 | — |
| EP-19 | GET | `/api/members/{id}` | 조회 | — |
| EP-20 | POST | `/api/auth/login` | 로그인 | — |
| EP-21 | POST | `/api/auth/reissue` | 재발급 | — |
| EP-22 | POST | `/api/auth/logout` | 로그아웃 | 예 |

**주소에 동사를 넣지 않는다.** 자원을 가리키고 방식으로 무엇을 할지 구분한다. 다만 마감 · 수락 · 거절은 상태 전이이므로 예외로 둔다.

**`me` 를 쓴다.** 회원 식별자를 받지 않고 토큰에서 확인한다. 받으면 남의 자료를 조회할 수 있다.

---

## 03. EP-01 · 모집글 목록 조회

| 항목 | 값 |
| --- | --- |
| 방식 | GET |
| 주소 | `/api/studies` |
| 인증 | 불필요 |
| 담당 | 1 |

### 요청

**질의 값**

| 이름 | 자료형 | 필수 | 기본값 | 설명 |
| --- | --- | --- | --- | --- |
| `page` | `int` | 아니오 | `0` | 쪽 번호 |
| `size` | `int` | 아니오 | `10` | 쪽당 건수 |
| `keyword` | `String` | 아니오 | | 제목 검색어 |
| `status` | `String` | 아니오 | | `RECRUITING` · `CLOSED` |

**예시**

```
GET /api/studies?page=0&size=10&keyword=자바&status=RECRUITING
```

### 응답 · 200

```json
{
  "content": [
    {
      "id": 1,
      "title": "자바 스터디 모집",
      "writerId": 1,
      "writerNickname": "홍길동",
      "capacity": 5,
      "acceptedCount": 2,
      "deadline": "2026-09-30",
      "status": "RECRUITING",
      "createdAt": "2026-09-03T10:15:30"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 25,
  "totalPages": 3,
  "first": true,
  "last": false
}
```

**목록에 소개를 담지 않는다.** 화면에 표시하지 않으며 분량만 늘어난다.

**자료가 없을 때**

```json
{
  "content": [],
  "page": 0,
  "size": 10,
  "totalElements": 0,
  "totalPages": 0,
  "first": true,
  "last": true
}
```

**! 검토 사항**

`acceptedCount` 를 모집글마다 따로 세면 조회 구문이 건수에 비례한다. NFR-04 를 만족하는지 확인이 필요하다.

---

## 04. EP-02 · 모집글 상세 조회

| 항목 | 값 |
| --- | --- |
| 방식 | GET |
| 주소 | `/api/studies/{id}` |
| 인증 | 불필요 |
| 담당 | 2 |

### 요청

**경로 값**

| 이름 | 자료형 | 설명 |
| --- | --- | --- |
| `id` | `Long` | 모집글 식별자 |

### 응답 · 200

```json
{
  "id": 1,
  "title": "자바 스터디 모집",
  "content": "매주 토요일 오후에 모입니다.",
  "capacity": 5,
  "acceptedCount": 2,
  "deadline": "2026-09-30",
  "status": "RECRUITING",
  "writerId": 1,
  "writerNickname": "홍길동",
  "createdAt": "2026-09-03T10:15:30",
  "updatedAt": null
}
```

**후기 건수를 담지 않는다.** 후기는 담당 5 의 영역이며, 담당 5 가 후기를 등록하면 담당 2 의 응답이 바뀌는 구조를 만들지 않는다. 건수가 필요하면 화면이 후기 목록의 길이로 센다.

**마감된 모집글**

```json
{
  "id": 3,
  "title": "스프링 부트 심화 스터디",
  "content": "실무에서 쓰는 구조를 함께 살펴봅니다.",
  "capacity": 3,
  "acceptedCount": 3,
  "deadline": "2026-09-20",
  "status": "CLOSED",
  "writerId": 1,
  "writerNickname": "홍길동",
  "createdAt": "2026-08-28T11:00:00",
  "updatedAt": "2026-09-01T09:30:00"
}
```

### 실패

| 응답 | 코드 | 사유 |
| --- | --- | --- |
| 404 | `NOT_FOUND` | 대상 부재 |

```json
{
  "status": 404,
  "code": "NOT_FOUND",
  "message": "모집글 부재",
  "fields": null,
  "timestamp": "2026-09-04T10:15:30"
}
```

---

## 05. EP-03 · 모집글 등록

| 항목 | 값 |
| --- | --- |
| 방식 | POST |
| 주소 | `/api/studies` |
| 인증 | 필요 |
| 담당 | 2 |

### 요청

**본문**

```json
{
  "title": "자바 스터디 모집",
  "content": "매주 토요일 오후에 모입니다.",
  "capacity": 5,
  "deadline": "2026-09-30"
}
```

| 항목 | 자료형 | 필수 | 제약 |
| --- | --- | --- | --- |
| `title` | `String` | 예 | 200자 이하 |
| `content` | `String` | 예 | |
| `capacity` | `int` | 예 | 1 이상 |
| `deadline` | `LocalDate` | 예 | 오늘 이후 |

**모집자를 본문에 담지 않는다.** 토큰에서 확인하며 받으면 위조가 가능하다.

### 응답 · 201

| 머리 | 값 |
| --- | --- |
| `Location` | `/api/studies/1` |

상세 조회와 같은 형태를 반환한다.

```json
{
  "id": 1,
  "title": "자바 스터디 모집",
  "content": "매주 토요일 오후에 모입니다.",
  "capacity": 5,
  "acceptedCount": 0,
  "deadline": "2026-09-30",
  "status": "RECRUITING",
  "writerId": 1,
  "writerNickname": "홍길동",
  "createdAt": "2026-09-03T10:15:30",
  "updatedAt": null
}
```

**`acceptedCount` 가 0 이고 `status` 가 `RECRUITING` 이다.** 등록 직후의 상태이다.

### 실패

| 응답 | 코드 | 사유 |
| --- | --- | --- |
| 400 | `INVALID_INPUT` | 검증 실패 |
| 401 | `UNAUTHORIZED` | 인증 부재 |

**검증 실패**

```json
{
  "status": 400,
  "code": "INVALID_INPUT",
  "message": "입력값 확인 필요",
  "fields": {
    "title": "제목은 필수",
    "capacity": "정원은 1 이상"
  },
  "timestamp": "2026-09-04T10:15:30"
}
```

**인증 부재**

```json
{
  "status": 401,
  "code": "UNAUTHORIZED",
  "message": "인증 필요",
  "fields": null,
  "timestamp": "2026-09-04T10:15:30"
}
```

---

## 06. EP-04 · 모집글 수정

| 항목 | 값 |
| --- | --- |
| 방식 | PUT |
| 주소 | `/api/studies/{id}` |
| 인증 | 필요 |
| 담당 | 2 |

### 요청

**본문은 등록과 같다.**

```json
{
  "title": "자바 스터디 모집 · 인원 추가",
  "content": "매주 토요일 오후에 모입니다. 인원을 늘렸습니다.",
  "capacity": 6,
  "deadline": "2026-10-05"
}
```

### 응답 · 200

상세 조회와 같은 형태를 반환하며 `updatedAt` 이 채워진다.

```json
{
  "id": 1,
  "title": "자바 스터디 모집 · 인원 추가",
  "content": "매주 토요일 오후에 모입니다. 인원을 늘렸습니다.",
  "capacity": 6,
  "acceptedCount": 2,
  "deadline": "2026-10-05",
  "status": "RECRUITING",
  "writerId": 1,
  "writerNickname": "홍길동",
  "createdAt": "2026-09-03T10:15:30",
  "updatedAt": "2026-09-04T14:20:00"
}
```

### 실패

| 응답 | 코드 | 사유 |
| --- | --- | --- |
| 400 | `INVALID_INPUT` | 검증 실패 |
| 400 | `STUDY_CLOSED` | 마감된 모집글 |
| 400 | `CAPACITY_BELOW_ACCEPTED` | 정원이 수락 인원보다 작음 |
| 401 | `UNAUTHORIZED` | 인증 부재 |
| 403 | `FORBIDDEN` | 모집자가 아님 |
| 404 | `NOT_FOUND` | 대상 부재 |

**마감된 모집글**

```json
{
  "status": 400,
  "code": "STUDY_CLOSED",
  "message": "마감된 모집글은 수정 불가",
  "fields": null,
  "timestamp": "2026-09-04T14:20:00"
}
```

**정원이 수락 인원보다 작음**

```json
{
  "status": 400,
  "code": "CAPACITY_BELOW_ACCEPTED",
  "message": "정원은 현재 수락 인원 3명보다 작을 수 없음",
  "fields": null,
  "timestamp": "2026-09-04T14:20:00"
}
```

**모집자가 아님**

```json
{
  "status": 403,
  "code": "FORBIDDEN",
  "message": "모집자만 수정 가능",
  "fields": null,
  "timestamp": "2026-09-04T14:20:00"
}
```

**마감된 모집글은 수정할 수 없다.** 정원을 늘리면 자리가 있는데 신청이 막히고, 마감일을 바꿔도 상태가 그대로라 의미가 없다.

**정원은 현재 수락 인원보다 작게 바꿀 수 없다.** 인원이 정원을 넘는 상태가 되며 되돌릴 방법이 없다.

---

## 07. EP-06 · 모집 마감

| 항목 | 값 |
| --- | --- |
| 방식 | PATCH |
| 주소 | `/api/studies/{id}/close` |
| 인증 | 필요 |
| 담당 | 2 |

### 요청

**본문이 없다.** 주소만으로 무엇을 할지 결정한다.

```
PATCH /api/studies/1/close
Authorization: Bearer eyJhbGciOi...
```

### 응답 · 200

상세 조회와 같은 형태를 반환하며 `status` 가 `CLOSED` 로 바뀐다.

```json
{
  "id": 1,
  "title": "자바 스터디 모집",
  "content": "매주 토요일 오후에 모입니다.",
  "capacity": 5,
  "acceptedCount": 2,
  "deadline": "2026-09-30",
  "status": "CLOSED",
  "writerId": 1,
  "writerNickname": "홍길동",
  "createdAt": "2026-09-03T10:15:30",
  "updatedAt": null
}
```

**`updatedAt` 이 채워지지 않는다.** 상태 전이는 내용 수정이 아니다.

### 실패

| 응답 | 코드 | 사유 |
| --- | --- | --- |
| 400 | `STUDY_CLOSED` | 이미 마감 |
| 403 | `FORBIDDEN` | 모집자가 아님 |
| 404 | `NOT_FOUND` | 대상 부재 |

```json
{
  "status": 400,
  "code": "STUDY_CLOSED",
  "message": "이미 마감된 모집글",
  "fields": null,
  "timestamp": "2026-09-04T14:20:00"
}
```

---
## 08. EP-07 · 신청

| 항목 | 값 |
| --- | --- |
| 방식 | POST |
| 주소 | `/api/studies/{studyId}/applications` |
| 인증 | 필요 |
| 담당 | 3 |

### 요청

**본문**

```json
{
  "message": "참여하고 싶습니다."
}
```

| 항목 | 자료형 | 필수 | 제약 |
| --- | --- | --- | --- |
| `message` | `String` | 아니오 | 300자 이하 |

### 응답 · 201

```json
{
  "id": 1,
  "studyPostId": 1,
  "studyPostTitle": "자바 스터디 모집",
  "applicantId": 2,
  "applicantNickname": "김철수",
  "status": "PENDING",
  "message": "참여하고 싶습니다.",
  "createdAt": "2026-09-03T11:20:00"
}
```

### 실패

| 응답 | 코드 | 사유 |
| --- | --- | --- |
| 400 | `SELF_APPLICATION` | 자기 모집글 |
| 400 | `STUDY_CLOSED` | 마감된 모집글 |
| 400 | `DEADLINE_PASSED` | 마감일이 지남 |
| 400 | `DUPLICATE_APPLICATION` | 이미 신청함 |
| 401 | `UNAUTHORIZED` | 인증 부재 |
| 404 | `NOT_FOUND` | 대상 부재 |

**자기 모집글**

```json
{
  "status": 400,
  "code": "SELF_APPLICATION",
  "message": "자기 모집글에는 신청 불가",
  "fields": null,
  "timestamp": "2026-09-04T11:20:00"
}
```

**마감된 모집글**

```json
{
  "status": 400,
  "code": "STUDY_CLOSED",
  "message": "마감된 모집글",
  "fields": null,
  "timestamp": "2026-09-04T11:20:00"
}
```

**마감일이 지남**

```json
{
  "status": 400,
  "code": "DEADLINE_PASSED",
  "message": "마감일이 지난 모집글",
  "fields": null,
  "timestamp": "2026-09-04T11:20:00"
}
```

**이미 신청함**

```json
{
  "status": 400,
  "code": "DUPLICATE_APPLICATION",
  "message": "이미 신청한 모집글",
  "fields": null,
  "timestamp": "2026-09-04T11:20:00"
}
```

**사유를 코드로 구분한다.** 응답 코드는 넷 다 400이므로 화면이 구분할 수 없다.

**판단 순서를 지킨다.** 대상 조회 → 자기 모집글 → 상태 → 마감일 → 중복 순이다.

---

## 09. EP-08 · 신청 취소

| 항목 | 값 |
| --- | --- |
| 방식 | DELETE |
| 주소 | `/api/applications/{id}` |
| 인증 | 필요 |
| 담당 | 3 |

### 요청

**본문이 없다.**

```
DELETE /api/applications/1
Authorization: Bearer eyJhbGciOi...
```

### 응답 · 204

본문이 없다.

### 실패

| 응답 | 코드 | 사유 |
| --- | --- | --- |
| 400 | `ALREADY_PROCESSED` | 대기 상태가 아님 |
| 401 | `UNAUTHORIZED` | 인증 부재 |
| 403 | `FORBIDDEN` | 신청자가 아님 |
| 404 | `NOT_FOUND` | 대상 부재 |

```json
{
  "status": 400,
  "code": "ALREADY_PROCESSED",
  "message": "이미 처리된 신청",
  "fields": null,
  "timestamp": "2026-09-04T11:20:00"
}
```

**대기 상태만 취소할 수 있다.** 수락된 신청을 취소하면 마감된 모집글에 빈자리가 생기며, 마감은 되돌리는 전이가 없어 정합 상태로 만들 수 없다.

**팀 판단 · BR-15 미채택 시** 응답을 200으로 두고 `status` 가 `CANCELED` 인 신청 응답을 반환한다.

```json
{
  "id": 1,
  "studyPostId": 1,
  "studyPostTitle": "자바 스터디 모집",
  "applicantId": 2,
  "applicantNickname": "김철수",
  "status": "CANCELED",
  "message": "참여하고 싶습니다.",
  "createdAt": "2026-09-03T11:20:00"
}
```

---

## 10. EP-09 · 신청 목록 조회

| 항목 | 값 |
| --- | --- |
| 방식 | GET |
| 주소 | `/api/studies/{studyId}/applications` |
| 인증 | 필요 |
| 담당 | 4 |

### 요청

```
GET /api/studies/1/applications
Authorization: Bearer eyJhbGciOi...
```

**쪽 단위로 나누지 않는다.** 한 모집글의 신청 수가 많지 않다.

### 응답 · 200

```json
[
  {
    "id": 1,
    "studyPostId": 1,
    "studyPostTitle": "자바 스터디 모집",
    "applicantId": 2,
    "applicantNickname": "김철수",
    "status": "PENDING",
    "message": "참여하고 싶습니다.",
    "createdAt": "2026-09-03T11:20:00"
  }
]
```

### 실패

| 응답 | 코드 | 사유 |
| --- | --- | --- |
| 401 | `UNAUTHORIZED` | 인증 부재 |
| 403 | `FORBIDDEN` | 모집자가 아님 |
| 404 | `NOT_FOUND` | 대상 부재 |

```json
{
  "status": 403,
  "code": "FORBIDDEN",
  "message": "모집자만 조회 가능",
  "fields": null,
  "timestamp": "2026-09-04T11:20:00"
}
```

**모집자만 조회할 수 있다.** 다른 사람이 요청하면 403을 반환한다.

**신청이 없을 때**

```json
[]
```

---

## 11. EP-10 · 신청 수락

| 항목 | 값 |
| --- | --- |
| 방식 | PATCH |
| 주소 | `/api/applications/{id}/accept` |
| 인증 | 필요 |
| 담당 | 4 |

### 요청

**본문이 없다.**

```
PATCH /api/applications/1/accept
Authorization: Bearer eyJhbGciOi...
```

### 응답 · 200

신청 응답과 같은 형태를 반환하며 `status` 가 `ACCEPTED` 로 바뀐다.

```json
{
  "id": 1,
  "studyPostId": 1,
  "studyPostTitle": "자바 스터디 모집",
  "applicantId": 2,
  "applicantNickname": "김철수",
  "status": "ACCEPTED",
  "message": "참여하고 싶습니다.",
  "createdAt": "2026-09-03T11:20:00"
}
```

### 실패

| 응답 | 코드 | 사유 |
| --- | --- | --- |
| 400 | `CAPACITY_EXCEEDED` | 정원이 참 |
| 400 | `ALREADY_PROCESSED` | 대기 상태가 아님 |
| 403 | `FORBIDDEN` | 모집자가 아님 |
| 404 | `NOT_FOUND` | 대상 부재 |

**정원이 참**

```json
{
  "status": 400,
  "code": "CAPACITY_EXCEEDED",
  "message": "정원이 찬 모집글",
  "fields": null,
  "timestamp": "2026-09-04T11:20:00"
}
```

**이미 처리된 신청**

```json
{
  "status": 400,
  "code": "ALREADY_PROCESSED",
  "message": "이미 처리된 신청",
  "fields": null,
  "timestamp": "2026-09-04T11:20:00"
}
```

**정원의 마지막 자리를 채우면 모집글도 마감된다.** 별도 호출이 필요 없다. 화면은 수락 후 상세를 다시 조회해 상태를 갱신한다.

```
  수락 응답        status ACCEPTED
      ▼
  상세 재조회      status CLOSED · acceptedCount 5
```

---

## 12. EP-11 · 신청 거절

| 항목 | 값 |
| --- | --- |
| 방식 | PATCH |
| 주소 | `/api/applications/{id}/reject` |
| 인증 | 필요 |
| 담당 | 4 |

### 요청

**본문이 없다.**

```
PATCH /api/applications/1/reject
Authorization: Bearer eyJhbGciOi...
```

### 응답 · 200

신청 응답과 같은 형태를 반환하며 `status` 가 `REJECTED` 로 바뀐다.

```json
{
  "id": 4,
  "studyPostId": 1,
  "studyPostTitle": "자바 스터디 모집",
  "applicantId": 5,
  "applicantNickname": "최지은",
  "status": "REJECTED",
  "message": "가능하면 참여하고 싶습니다.",
  "createdAt": "2026-09-03T12:30:00"
}
```

### 실패

| 응답 | 코드 | 사유 |
| --- | --- | --- |
| 400 | `ALREADY_PROCESSED` | 대기 상태가 아님 |
| 403 | `FORBIDDEN` | 모집자가 아님 |
| 404 | `NOT_FOUND` | 대상 부재 |

```json
{
  "status": 400,
  "code": "ALREADY_PROCESSED",
  "message": "이미 처리된 신청",
  "fields": null,
  "timestamp": "2026-09-04T11:20:00"
}
```

**정원을 확인하지 않는다.** 거절은 인원에 영향을 주지 않는다.

---
## 13. EP-12 · 후기 목록 조회

| 항목 | 값 |
| --- | --- |
| 방식 | GET |
| 주소 | `/api/studies/{studyId}/reviews` |
| 인증 | 불필요 |
| 담당 | 5 |

### 요청

```
GET /api/studies/3/reviews
```

**인증이 필요 없다.** 손님도 후기를 읽을 수 있다.

### 응답 · 200

```json
[
  {
    "id": 1,
    "studyPostId": 1,
    "content": "많이 배웠습니다.",
    "rating": 5,
    "writerId": 2,
    "writerNickname": "김철수",
    "createdAt": "2026-09-20T14:00:00"
  }
]
```

**`writerId` 를 담는다.** 화면이 자기 후기에만 삭제 단추를 노출하기 위해 필요하다.

**후기가 없을 때**

```json
[]
```

### 실패

| 응답 | 코드 | 사유 |
| --- | --- | --- |
| 404 | `NOT_FOUND` | 대상 부재 |

---

## 14. EP-13 · 후기 등록

| 항목 | 값 |
| --- | --- |
| 방식 | POST |
| 주소 | `/api/studies/{studyId}/reviews` |
| 인증 | 필요 |
| 담당 | 5 |

### 요청

**본문**

```json
{
  "content": "많이 배웠습니다.",
  "rating": 5
}
```

| 항목 | 자료형 | 필수 | 제약 |
| --- | --- | --- | --- |
| `content` | `String` | 예 | 500자 이하 |
| `rating` | `int` | 예 | 1 이상 5 이하 |

### 응답 · 201

후기 목록의 항목과 같은 형태를 반환한다.

```json
{
  "id": 1,
  "studyPostId": 3,
  "content": "많이 배웠습니다.",
  "rating": 5,
  "writerId": 2,
  "writerNickname": "김철수",
  "createdAt": "2026-09-20T14:00:00"
}
```

### 실패

| 응답 | 코드 | 사유 |
| --- | --- | --- |
| 400 | `INVALID_INPUT` | 검증 실패 |
| 400 | `STUDY_NOT_CLOSED` | 마감되지 않음 |
| 400 | `DUPLICATE_REVIEW` | 이미 작성함 |
| 401 | `UNAUTHORIZED` | 인증 부재 |
| 403 | `FORBIDDEN` | 참여자가 아님 |
| 404 | `NOT_FOUND` | 대상 부재 |

**검증 실패**

```json
{
  "status": 400,
  "code": "INVALID_INPUT",
  "message": "입력값 확인 필요",
  "fields": {
    "rating": "평점은 1 이상 5 이하"
  },
  "timestamp": "2026-09-20T14:00:00"
}
```

**마감되지 않음**

```json
{
  "status": 400,
  "code": "STUDY_NOT_CLOSED",
  "message": "마감된 뒤에만 작성 가능",
  "fields": null,
  "timestamp": "2026-09-20T14:00:00"
}
```

**이미 작성함**

```json
{
  "status": 400,
  "code": "DUPLICATE_REVIEW",
  "message": "이미 작성한 후기",
  "fields": null,
  "timestamp": "2026-09-20T14:00:00"
}
```

**참여자가 아님**

```json
{
  "status": 403,
  "code": "FORBIDDEN",
  "message": "모집자와 수락된 참여자만 작성 가능",
  "fields": null,
  "timestamp": "2026-09-20T14:00:00"
}
```

**모집자도 작성할 수 있다.** 모집자는 신청 절차를 거치지 않아 수락된 신청이 없으므로 별도로 허용한다.

**팀 판단 · BR-14 미채택 시** `DUPLICATE_REVIEW` 줄을 지운다.

---

## 15. EP-14 · 후기 삭제

| 항목 | 값 |
| --- | --- |
| 방식 | DELETE |
| 주소 | `/api/reviews/{id}` |
| 인증 | 필요 |
| 담당 | 5 |

### 요청

**본문이 없다.**

```
DELETE /api/reviews/1
Authorization: Bearer eyJhbGciOi...
```

### 응답 · 204

본문이 없다.

### 실패

| 응답 | 코드 | 사유 |
| --- | --- | --- |
| 401 | `UNAUTHORIZED` | 인증 부재 |
| 403 | `FORBIDDEN` | 작성자가 아님 |
| 404 | `NOT_FOUND` | 대상 부재 |

```json
{
  "status": 403,
  "code": "FORBIDDEN",
  "message": "작성자만 삭제 가능",
  "fields": null,
  "timestamp": "2026-09-20T14:00:00"
}
```

**모집자에게 삭제 권한을 주지 않는다.** 낮은 평점을 지울 수 있게 되어 후기의 의미가 사라진다.

---

## 16. EP-15 · 내 정보 조회

| 항목 | 값 |
| --- | --- |
| 방식 | GET |
| 주소 | `/api/members/me` |
| 인증 | 필요 |
| 담당 | 6 |

### 요청

```
GET /api/members/me
Authorization: Bearer eyJhbGciOi...
```

### 응답 · 200

```json
{
  "id": 1,
  "email": "test@example.com",
  "nickname": "홍길동",
  "createdAt": "2026-08-01T09:00:00"
}
```

**비밀번호를 담지 않는다.** 변환된 값이라도 응답에 넣지 않는다.

### 실패

| 응답 | 코드 | 사유 |
| --- | --- | --- |
| 401 | `UNAUTHORIZED` | 인증 부재 |

```json
{
  "status": 401,
  "code": "UNAUTHORIZED",
  "message": "인증 필요",
  "fields": null,
  "timestamp": "2026-09-04T10:15:30"
}
```

---

## 17. EP-16 · 내 모집글 목록

| 항목 | 값 |
| --- | --- |
| 방식 | GET |
| 주소 | `/api/members/me/studies` |
| 인증 | 필요 |
| 담당 | 6 |

### 요청

```
GET /api/members/me/studies
Authorization: Bearer eyJhbGciOi...
```

### 응답 · 200

모집글 목록의 항목과 같은 형태를 배열로 반환한다. 최신순으로 정렬한다.

```json
[
  {
    "id": 1,
    "title": "자바 스터디 모집",
    "writerId": 1,
    "writerNickname": "홍길동",
    "capacity": 5,
    "acceptedCount": 2,
    "deadline": "2026-09-30",
    "status": "RECRUITING",
    "createdAt": "2026-09-03T10:15:30"
  }
]
```

**등록한 모집글이 없을 때**

```json
[]
```

**쪽 단위로 나누지 않는다.** 한 사람이 등록하는 모집글 수가 많지 않다.

### 실패

| 응답 | 코드 | 사유 |
| --- | --- | --- |
| 401 | `UNAUTHORIZED` | 인증 부재 |

---

## 18. EP-17 · 내 신청 목록

| 항목 | 값 |
| --- | --- |
| 방식 | GET |
| 주소 | `/api/members/me/applications` |
| 인증 | 필요 |
| 담당 | 6 |

### 요청

```
GET /api/members/me/applications
Authorization: Bearer eyJhbGciOi...
```

### 응답 · 200

신청 응답 목록을 반환한다. 최신순으로 정렬한다.

```json
[
  {
    "id": 3,
    "studyPostId": 5,
    "studyPostTitle": "알고리즘 스터디",
    "applicantId": 2,
    "applicantNickname": "김철수",
    "status": "ACCEPTED",
    "message": "",
    "createdAt": "2026-09-05T09:30:00"
  }
]
```

**신청한 내역이 없을 때**

```json
[]
```

**모집글 제목을 담으므로 함께 조회를 지정한다.** 지정하지 않으면 신청 건수만큼 조회가 늘어난다.

### 실패

| 응답 | 코드 | 사유 |
| --- | --- | --- |
| 401 | `UNAUTHORIZED` | 인증 부재 |

---

## 19. EP-20 · 로그인 · 제공

| 항목 | 값 |
| --- | --- |
| 방식 | POST |
| 주소 | `/api/auth/login` |
| 인증 | 불필요 |

### 요청

```json
{
  "email": "test@example.com",
  "password": "Study!1234"
}
```

### 응답 · 200

```json
{
  "accessToken": "eyJhbGciOi...",
  "refreshToken": "eyJhbGciOi...",
  "memberId": 1,
  "nickname": "홍길동"
}
```

| 항목 | 용도 |
| --- | --- |
| `accessToken` | 인증이 필요한 요청의 머리에 넣는다 |
| `refreshToken` | 접근 토큰 재발급에 쓴다 |
| `memberId` | 응답의 `writerId` 와 비교해 본인 여부를 판단한다 |
| `nickname` | 머리 영역에 표시한다 |

**`memberId` 가 담당 2 · 3 · 4 · 5 의 분기 조건이다.** 이 값이 없으면 화면이 본인 여부를 알 수 없다.

### 실패

| 응답 | 코드 | 사유 |
| --- | --- | --- |
| 401 | `UNAUTHORIZED` | 이메일 또는 비밀번호 불일치 |

```json
{
  "status": 401,
  "code": "UNAUTHORIZED",
  "message": "이메일 또는 비밀번호가 올바르지 않음",
  "fields": null,
  "timestamp": "2026-09-04T09:00:00"
}
```

**둘을 구분해 알리지 않는다.** 구분하면 가입 여부가 드러난다.

---

## 20. EP-22 · 로그아웃 · 제공

| 항목 | 값 |
| --- | --- |
| 방식 | POST |
| 주소 | `/api/auth/logout` |
| 인증 | 필요 |

### 요청

**본문이 없다.**

```
POST /api/auth/logout
Authorization: Bearer eyJhbGciOi...
```

### 응답 · 204

본문이 없다.

**갱신 토큰을 보관하지 않으므로 즉시 무효화되지 않는다.** 화면이 보관한 토큰을 지우는 것이 실제 효과이며, 복사해 둔 토큰은 만료까지 유효하다. 알고 남긴 문제이며 제출용 안내에 함께 적는다.

```
  로그아웃하면 화면에서 토큰을 제거
      ▼
  그 토큰 자체는 만료까지 유효
      ▼
  복사해 둔 값으로 계속 사용 가능
```

---

## 21. 명세와 구현의 관계

이 문서에 적힌 항목 이름이 곧 계약이다.

```
  명세에 writerNickname 이라고 적음
      ▼
  서버가 그 이름으로 응답
      ▼
  화면이 그 이름으로 읽음
      ▼
  하나라도 다르면 값이 표시되지 않음
```

**서버 담당과 화면 담당이 이 문서만 보고 각자 작업한다.** 이름 하나를 임의로 바꾸면 상대의 작업이 동작하지 않는다.

### 변경 절차

| 상황 | 처리 |
| --- | --- |
| 항목 이름 변경 | 문서를 먼저 고치고 양쪽에 공유 |
| 항목 추가 | 추가 명세에 기록 |
| 주소 변경 | 문서를 먼저 고치고 양쪽에 공유 |
| 실패 코드 추가 | 화면 안내 문구표에도 함께 추가 |

### 팀 판단이 명세에 미치는 영향

| 항목 | 미채택 시 |
| --- | --- |
| BR-13 | 신청 시 확인 대상이 대기와 수락으로 줄어든다 |
| BR-14 | `DUPLICATE_REVIEW` 를 쓰지 않는다 |
| BR-15 | EP-08 응답이 204에서 200으로 바뀐다 |

**팀이 선택을 바꾸면 이 문서를 함께 고친다.** 화면 담당이 문서를 기준으로 작업하므로 구현만 바꾸면 어긋난다.

---

&copy; SUPERB DEVOPS. All rights reserved.
