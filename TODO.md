# TODO 안내

스켈레톤을 채워 스터디 모집 게시판을 완성함. TODO 는 44개이며 담당별로 번호 구간이 나뉨.

---

## 01. 주석 읽는 법

    /*
     * TODO 23 · 모집글 수정
     *
     * 기능        모집자 본인인지 → 모집 중인지 → 정원 확인 순서로 판단
     * 활용메소드  StudyService.getWithWriter()   같은 클래스 · 제공됨
     * 반환형태    StudyDetailResponse · TODO.md 응답 형태 참고
     * 동작결과    EP-04 · 정상 200 · 남의 글 403 · 마감된 글 400 STUDY_CLOSED
     */
    throw new UnsupportedOperationException("TODO 23");

| 줄 | 뜻 | 이렇게 씀 |
| --- | --- | --- |
| 기능 | 이 자리에서 만들 것 | 적힌 순서대로 판단함 · 순서가 바뀌면 응답이 달라짐 |
| 활용메소드 | 이미 있어서 가져다 쓸 것 | 새로 만들지 말고 찾아 씀 |
| 반환형태 | 무엇을 돌려주는가 | 서버 쪽에만 있음 |
| 받는자료 | 서버에서 무엇이 오는가 | 화면 쪽에만 있음 |
| 그릴위치 | 어느 화면 어느 요소에 그리는가 | 화면 쪽에만 있음 |
| 동작결과 | 만들고 나면 이렇게 됨 | Postman 이나 화면으로 직접 확인 |

### 활용메소드 옆의 표시

| 표시 | 뜻 |
| --- | --- |
| 제공됨 | 이미 만들어져 있음 · 찾아서 씀 |
| TODO N · 같은 담당 | 내가 먼저 만들어야 함 |
| TODO N · 담당 M | 그 담당이 만들면 쓸 수 있음 |
| 없음 | 직접 만들면 됨 |

### 다 만들면

**주석을 지움.** 아래쪽 도구 창의 `TODO` 탭에서 남은 개수를 확인 가능함.

    TODO 23 · 모집글 수정          StudyService.java
    TODO 24 · 모집글 삭제          StudyService.java

---

## 02. 담당 간 연결

**여러 담당이 함께 쓰는 조회는 미리 만들어져 있음.** 먼저 끝내야 다른 사람이 시작할 수 있는 자리를 없앴음.

| 제공된 것 | 누가 쓰는가 |
| --- | --- |
| `StudyPostRepository.findWithWriterById()` | 담당 2 · 3 · 4 · 5 |
| `StudyService.getWithWriter()` | 담당 2 · 3 · 4 · 5 |
| `ApplicationRepository.findWithStudyPostById()` | 담당 3 · 4 |
| `ApplicationService.getWithStudyPost()` | 담당 3 · 4 |

**여섯이 동시에 시작 가능함.**

### 남아 있는 연결

화면 쪽에 둘이 남음.

```
  담당 2  TODO 26  모집글 주소
      │
      └──▶ 담당 1  TODO 16 · 17  등록 수정 화면이 이 주소를 부름

  담당 6  TODO 65  내 자료 주소
      │
      └──▶ 담당 3  TODO 35  내 신청 상태를 여기서 받음
```

**둘 다 기다리지 않아도 됨.** 응답 형태가 아래에 있으므로 값을 직접 넣어 화면부터 만들면 됨.

```javascript
// 서버가 아직 없을 때 이 값으로 화면을 먼저 만듦
const sample = { id: 1, title: "자바 스터디 모집", status: "RECRUITING" };
```

## 03. 진행 순서

각자 아래 순서로 진행함. 아래로 갈수록 앞의 것이 있어야 확인 가능함.

```
  1  저장소 규약        가장 쉬움 · 이름만 지으면 됨
        ▼
  2  업무 계층          규칙 판단이 들어감
        ▼
  3  표현 계층          주소와 응답 코드
        ▼
  4  화면              서버가 되어야 확인 가능
```

**화면을 먼저 만들 수도 있음.** 응답 형태가 아래에 있으므로 값을 직접 넣어 모양부터 잡으면 됨.

```javascript
// 서버가 아직 없을 때 이 값으로 화면을 먼저 만듦
const sample = { id: 1, title: "자바 스터디 모집", status: "RECRUITING" };
```

---

## 04. 응답 형태

### StudyListResponse

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

`status` 는 `RECRUITING` · `CLOSED`

### PageResponse

목록은 이 형태로 감싸여 옴.

    {
      "content": [ ... StudyListResponse 목록 ... ],
      "page": 0,
      "size": 10,
      "totalElements": 23,
      "totalPages": 3,
      "first": true,
      "last": false
    }

### StudyDetailResponse

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

`writerId` 를 로그인한 회원 식별자와 비교해 모집자 여부를 판단함.

### ApplicationResponse

    {
      "id": 3,
      "studyPostId": 1,
      "studyPostTitle": "자바 스터디 모집",
      "applicantId": 2,
      "applicantNickname": "김철수",
      "status": "PENDING",
      "message": "참여하고 싶습니다",
      "createdAt": "2026-09-03T11:20:00"
    }

`status` 는 `PENDING` · `ACCEPTED` · `REJECTED`

### ReviewResponse

    {
      "id": 7,
      "studyPostId": 1,
      "content": "많이 배웠습니다.",
      "rating": 5,
      "writerId": 2,
      "writerNickname": "김철수",
      "createdAt": "2026-09-20T14:00:00"
    }

`writerId` 로 자기 후기인지 판단해 삭제 단추를 가름.

### MemberResponse

    {
      "id": 1,
      "email": "test@example.com",
      "nickname": "홍길동",
      "createdAt": "2026-08-01T09:00:00"
    }

### ErrorResponse

실패는 모두 이 형태로 옴.

    {
      "status": 400,
      "code": "DUPLICATE_APPLICATION",
      "message": "이미 신청한 모집글",
      "fields": null,
      "timestamp": "2026-09-04T10:15:30"
    }

검증 실패일 때만 `fields` 가 채워짐.

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

| 코드 | 응답 | 사유 |
| --- | --- | --- |
| `INVALID_INPUT` | 400 | 검증 실패 |
| `DUPLICATE_APPLICATION` | 400 | 이미 신청함 |
| `STUDY_CLOSED` | 400 | 마감된 모집글 |
| `DEADLINE_PASSED` | 400 | 마감일이 지남 |
| `SELF_APPLICATION` | 400 | 자기 모집글 |
| `CAPACITY_EXCEEDED` | 400 | 정원이 참 |
| `CAPACITY_BELOW_ACCEPTED` | 400 | 정원이 수락 인원보다 작음 |
| `ALREADY_PROCESSED` | 400 | 이미 처리된 신청 |
| `STUDY_NOT_CLOSED` | 400 | 마감되지 않은 스터디 |
| `DUPLICATE_REVIEW` | 400 | 이미 작성한 후기 |
| `UNAUTHORIZED` | 401 | 인증 부재 또는 실패 |
| `FORBIDDEN` | 403 | 권한 부족 |
| `NOT_FOUND` | 404 | 대상 부재 |
| `INTERNAL_ERROR` | 500 | 처리하지 못한 문제 |

---

## 05. 화면 도구

`common.js` 와 `api.js` 에 이미 만들어져 있음. **직접 만들지 않음.**

| 도구 | 하는 일 | 예 |
| --- | --- | --- |
| `badge(status)` | 상태 배지를 만듦 | `badge('PENDING')` |
| `shortDate(값)` | 날짜를 짧게 | `09-30` |
| `dateTime(값)` | 날짜와 시각 | `09-03 10:15` |
| `escapeHtml(값)` | 특수 문자 처리 | 화면에 넣기 전 필수 |
| `param(이름)` | 주소의 질의 값 | `param('id')` |
| `requireLogin()` | 로그인 확인 | 아니면 로그인 화면으로 |
| `showError(요소, 오류)` | 실패 안내 표시 | |
| `showFieldErrors(오류, 접두)` | 입력란별 사유 | 사유가 없으면 `false` |
| `auth.loggedIn` | 로그인 여부 | |
| `auth.memberId` | 내 회원 식별자 | 소유 판단에 씀 |
| `api.get(주소)` | 조회 | 토큰이 자동으로 붙음 |
| `api.post(주소, 본문)` | 등록 | |
| `api.put` · `api.patch` · `api.del` | 수정 · 상태 변경 · 삭제 | |

### 상세 화면 조율

상세 화면은 네 담당이 나눠 씀. `study-page.js` 가 자료를 한 번만 조회해 넷이 함께 씀.

| 도구 | 하는 일 |
| --- | --- |
| `StudyPage.study` | 상세 응답 |
| `StudyPage.myApplication` | 내 신청 · 없으면 `null` |
| `StudyPage.isOwner()` | 모집자 본인인지 |
| `StudyPage.id` | 주소의 모집글 식별자 |
| `StudyPage.reload()` | 자료를 다시 읽고 네 구획을 다시 그림 |
| `StudyPage.register(함수)` | 표시 함수를 등록 |

**자료가 바뀌면 `StudyPage.reload()` 를 부름.** 다른 담당의 함수를 알 필요가 없음.

### 화면 조각

`/parts.html` 을 브라우저로 열면 조각의 실제 모양이 보임. 복사해 값만 바꿔 씀.

```
http://localhost:8090/parts.html
```

**클래스 이름을 바꾸면 모양이 깨짐.**

---

## 06. 담당별 표

### 담당 1 · 목록과 등록 화면

| 번호 | 파일 | 하는 일 | 활용 | 명세 |
| --- | --- | --- | --- | --- |
| 11 | StudyService | 모집글 목록 조회 | `search` · `acceptedCounts` | FR-01 · EP-01 |
| 12 | StudyController | 모집글 목록 주소 | 11 | EP-01 |
| 13 | list.js | 목록 표시 | 15 | SC-01 |
| 14 | list.js | 쪽 이동 표시 | 15 | SC-01 |
| 15 | list.js | 목록 조회 | 12 · 13 · 14 | SC-01 |
| 16 | form.js | 등록 · 수정 화면 준비 | 26 · 담당 2 | SC-03 |
| 17 | form.js | 저장 | 26 · 담당 2 | SC-03 · FR-05 · FR-06 |

### 담당 2 · 상세와 관리

| 번호 | 파일 | 하는 일 | 활용 | 명세 |
| --- | --- | --- | --- | --- |
| 21 | StudyService | 모집글 등록 | `getMember` | FR-05 · EP-03 |
| 22 | StudyService | 모집글 상세 조회 | `getWithWriter` · `countAccepted` | FR-04 · EP-02 |
| 23 | StudyService | 모집글 수정 | `getWithWriter` · `countAccepted` | FR-06 · EP-04 · BR-11 · BR-12 |
| 24 | StudyService | 모집글 삭제 | `getWithWriter` | FR-07 · EP-05 |
| 25 | StudyService | 모집 마감 | `getWithWriter` | FR-08 · EP-06 |
| 26 | StudyController | 모집글 주소 다섯 | 21 ~ 25 | EP-02 ~ EP-06 |
| 27 | study-detail.js | 상세 표시 | 26 | SC-02 |
| 28 | study-detail.js | 단추 동작 | 26 | SC-02 |

### 담당 3 · 신청

| 번호 | 파일 | 하는 일 | 활용 | 명세 |
| --- | --- | --- | --- | --- |
| 31 | ApplicationService | 신청 | `getWithWriter` · 제공된 조회 | FR-09 · EP-07 · BR-01 ~ BR-04 |
| 32 | ApplicationService | 신청 취소 | `getWithStudyPost` | FR-10 · EP-08 · BR-15 |
| 33 | ApplicationController | 신청과 취소 주소 | 31 · 32 | EP-07 · EP-08 |
| 34 | study-apply.js | 구획 표시 조건과 신청 전 화면 | 33 | SC-02 |
| 35 | study-apply.js | 신청 후 화면 | 64 · 담당 6 | SC-02 |

### 담당 4 · 신청 처리

| 번호 | 파일 | 하는 일 | 활용 | 명세 |
| --- | --- | --- | --- | --- |
| 41 | ApplicationRepository | 신청 목록 규약 | 없음 | DB 설계 |
| 42 | ApplicationService | 신청 목록 조회 | `getWithWriter` · 41 | FR-11 · EP-09 |
| 43 | ApplicationService | 신청 수락 | 45 | FR-12 · EP-10 · BR-02 · BR-05 |
| 44 | ApplicationService | 신청 거절 | 45 | FR-13 · EP-11 |
| 45 | ApplicationService | 처리 가능 확인 공통 | `getWithStudyPost` · 제공됨 | BR-08 · BR-10 |
| 46 | ApplicationController | 신청 처리 주소 셋 | 42 ~ 44 | EP-09 ~ EP-11 |
| 47 | study-application.js | 신청 목록 표시 | 46 | SC-02 |
| 48 | study-application.js | 수락과 거절 처리 | 46 | SC-02 |

### 담당 5 · 후기

| 번호 | 파일 | 하는 일 | 활용 | 명세 |
| --- | --- | --- | --- | --- |
| 51 | ReviewRepository | 후기 규약 | 없음 | DB 설계 |
| 52 | ReviewService | 후기 목록 조회 | 51 | FR-14 · EP-12 |
| 53 | ReviewService | 후기 등록 | `getWithWriter` · 55 | FR-15 · EP-13 · BR-06 · BR-07 · BR-14 |
| 54 | ReviewService | 후기 삭제 | 없음 | FR-16 · EP-14 |
| 55 | ReviewService | 참여자 확인 공통 | 없음 | BR-07 |
| 56 | ReviewController | 후기 주소 셋 | 52 ~ 54 | EP-12 ~ EP-14 |
| 57 | study-review.js | 후기 목록과 입력란 | 56 | SC-02 |
| 58 | study-review.js | 후기 등록과 삭제 | 56 | SC-02 |

### 담당 6 · 마이페이지

| 번호 | 파일 | 하는 일 | 활용 | 명세 |
| --- | --- | --- | --- | --- |
| 61 | StudyPostRepository | 내 모집글 규약 | 없음 | DB 설계 |
| 62 | ApplicationRepository | 내 신청 규약 | 없음 | DB 설계 |
| 63 | StudyService | 내 모집글 조회 | 61 · `acceptedCounts` | FR-18 · EP-16 |
| 64 | ApplicationService | 내 신청 조회 | 62 | FR-19 · EP-17 |
| 65 | MemberController | 내 자료 주소 셋 | 63 · 64 | EP-15 ~ EP-17 |
| 66 | mypage.js | 내 정보 표시 | 65 | SC-04 |
| 67 | mypage.js | 내 모집글 표시 | 65 | SC-04 |
| 68 | mypage.js | 내 신청 표시 | 65 | SC-04 |

---

## 07. 막혔을 때

**먼저 20분은 스스로 찾아봄.** 아래를 순서대로 확인함.

| 증상 | 확인 |
| --- | --- |
| `UnsupportedOperationException` | 그 번호의 TODO 가 아직 비어 있음 |
| 400 인데 사유를 모르겠음 | 응답의 `code` 를 확인 |
| 401 | 로그인 · 토큰이 붙는지 |
| 403 | 소유자 판단 · 남의 자료를 건드리고 있는지 |
| 404 | 주소 · 요청 방식 |
| 500 | 실행 창의 예외 확인 |
| 화면이 비어 있음 | 개발자 도구의 콘솔과 네트워크 확인 |
| 모양이 깨짐 | 클래스 이름 · `parts.html` 과 대조 |

---

&copy; SUPERB DEVOPS. All rights reserved.
