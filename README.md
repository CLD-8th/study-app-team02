# study-app-skeleton

스터디 모집 게시판 스켈레톤. TODO 45개를 채워 완성함.

## 구성

| 항목 | 값 |
| --- | --- |
| JDK | 21 |
| 프레임워크 | Spring Boot 4.1.0 |
| 데이터베이스 | MySQL 8.4 |
| 포트 | 8090 |
| 실행 구성 | `StudyApp` |
| 패키지 | `com.example.study` |

## 문서

| 파일 | 담는 것 |
| --- | --- |
| `README.md` | 저장소 구성과 준비 |
| `TODO.md` | 담당별 표 · 주석 읽는 법 · 응답 형태 |
| `docs/api.md` | 주소 · 요청 · 응답 · 실패 코드 |
| `docs/functions.md` | 기능별 판단 순서 · 상태 전이 · 권한 |

**주석의 `동작결과` 줄은 `docs/api.md`, `기능` 줄은 `docs/functions.md` 에서 찾음.**

## 준비

`db/schema.sql` 을 실행함.

| 항목 | 값 |
| --- | --- |
| 스키마 | `study_app` |
| 계정 | `study` |
| 비밀번호 | `Study!1234` |

**표는 기동할 때 만들어짐.** 직접 만들지 않아도 됨.


## 샘플 자료

**한 번 기동한 뒤** `db/sample.sql` 을 실행하면 확인용 자료가 들어감.

| 대상 | 건수 |
| --- | --- |
| 회원 | 5 |
| 모집글 | 6 · 모집 중 4 · 마감 2 |
| 신청 | 14 · 대기 · 수락 · 거절 |
| 후기 | 5 |

**모든 계정의 비밀번호는 `1234` 임.** `hong@example.com` 이 모집글 1 과 3 의 모집자임.

TODO 를 채우며 화면이 제대로 나오는지 확인할 때 씀. 자료가 없으면 빈 화면만 보여 확인이 어려움.

### 되돌리기

```sql
DELETE FROM review;
DELETE FROM application;
DELETE FROM study_post;
DELETE FROM member;
```
## 실행

**실행 구성 목록에서 `StudyApp` 을 선택해 기동.**

```
http://localhost:8090
```

**스켈레톤 상태에서도 기동됨.** 아직 채우지 않은 기능을 부르면 500 응답에 번호가 담김.

```json
{
  "status": 500,
  "code": "INTERNAL_ERROR",
  "message": "잠시 후 다시 시도"
}
```

실행 창에 어느 번호인지 나타남.

```
  UnsupportedOperationException: TODO 23
```

## 무엇을 채우는가

| 등급 | 대상 | 상태 |
| --- | --- | --- |
| 제공 | 엔티티 4 · 공통 6 · 인증 6 | 그대로 씀 |
| 제공 | 설정 · CSS · `api.js` · `common.js` | 그대로 씀 |
| 제공 | 로그인 · 가입 화면 | 그대로 씀 |
| 제공 | HTML 뼈대와 요소 id | **고치지 않음** |
| 채움 | 저장소 규약 · 업무 계층 · 표현 계층 | TODO |
| 채움 | 화면 넷의 표시 함수 | TODO |

**HTML 은 아무도 고치지 않음.** 요소 id 가 어긋나면 다른 담당의 구획을 덮어씀.

## 담당별 파일

| 담당 | 번호 | 파일 |
| --- | --- | --- |
| 1 | 11 ~ 17 | `StudyService` · `StudyController` · `list.js` · `form.js` |
| 2 | 21 ~ 28 | `StudyPostRepository` · `StudyService` · `StudyController` · `study-detail.js` |
| 3 | 31 ~ 35 | `ApplicationRepository` · `ApplicationService` · `ApplicationController` · `study-apply.js` |
| 4 | 41 ~ 48 | `ApplicationRepository` · `ApplicationService` · `ApplicationController` · `study-application.js` |
| 5 | 51 ~ 58 | `ReviewRepository` · `ReviewService` · `ReviewController` · `study-review.js` |
| 6 | 61 ~ 68 | `StudyPostRepository` · `ApplicationRepository` · `StudyService` · `ApplicationService` · `MemberController` · `mypage.js` |

**같은 파일을 둘이 쓰는 경우가 있음.** 서로 다른 메서드이므로 자기 번호만 건드림.

| 파일 | 담당 |
| --- | --- |
| `StudyPostRepository` | 2 · 6 |
| `ApplicationRepository` | 3 · 4 · 6 |
| `StudyService` | 1 · 2 · 6 |
| `ApplicationService` | 3 · 4 · 6 |
| `StudyController` | 1 · 2 |
| `ApplicationController` | 3 · 4 |

## 화면

| 번호 | 화면 | 주소 | 담당 |
| --- | --- | --- | --- |
| SC-01 | 모집글 목록 | `/index.html` | 1 |
| SC-02 | 모집글 상세 | `/study.html?id=` | 2 · 3 · 4 · 5 |
| SC-03 | 모집글 등록 · 수정 | `/form.html` | 1 |
| SC-04 | 마이페이지 | `/mypage.html` | 6 |
| SC-05 | 로그인 | `/login.html` | 제공 |
| SC-06 | 회원 가입 | `/signup.html` | 제공 |
| — | 화면 조각 모음 | `/parts.html` | 참고용 |

상세 화면은 네 구획으로 나뉨. 각자 자기 JS 파일만 건드림.

| 구획 | 요소 id | 파일 | 담당 |
| --- | --- | --- | --- |
| 모집글 상세 | `study-detail` | `study-detail.js` | 2 |
| 신청 | `apply-panel` | `study-apply.js` | 3 |
| 신청 목록 | `application-panel` | `study-application.js` | 4 |
| 후기 | `review-panel` | `study-review.js` | 5 |

## 진행 순서

```
  1  저장소 규약        가장 쉬움 · 이름만 지으면 됨
        ▼
  2  업무 계층          규칙 판단이 들어감
        ▼
  3  표현 계층          주소와 응답 코드
        ▼
  4  화면              서버가 되어야 확인 가능
```

**여섯이 동시에 시작 가능함.** 여러 담당이 함께 쓰는 조회는 미리 만들어져 있음.

## 주석에서 찾아갈 곳

| 주석 줄 | 찾을 곳 |
| --- | --- |
| 기능 | `docs/functions.md` · `FR-NN` |
| 활용메소드 | 소스 · 같은 담당 TODO |
| 반환형태 · 받는자료 | `TODO.md` 응답 형태 |
| 그릴위치 | `parts.html` · `SC-NN` |
| 동작결과 | `docs/api.md` · `EP-NN` |

업무 규칙 `BR-NN` 은 `docs/functions.md` 의 예외 처리와 팀 판단 절에 있음.

---

&copy; SUPERB DEVOPS. All rights reserved.
