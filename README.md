# 게시판 (Board)

Spring Boot 기반 게시판 애플리케이션. 회원 인증/인가, 댓글, 다중 단어 검색·페이징을 제공하며,
**JPA N+1 문제를 서브쿼리로 원천 제거**하고 단위·통합 테스트로 검증했습니다.

> 학습 목적으로 각 기능을 "왜 그렇게 구현하는가"를 이해하며 직접 작성했습니다.
> 단순 CRUD를 넘어 DTO 분리, 트랜잭션, 연관관계, 성능(N+1), 보안(인증·인가)까지 다룹니다.

---

## 기술 스택

| 구분 | 기술                                                  |
|------|-----------------------------------------------------|
| Language | Java 21                                             |
| Framework | Spring Boot 4.1.0, Spring Security, Spring Data JPA |
| Query | QueryDSL (OpenFeign fork)                           |
| DB | MySQL (운영) / H2 (테스트)                               |
| View | Thymeleaf                                           |
| Test | JUnit 5, Mockito, AssertJ                           |
| Build | Gradle                                              |


---

## 주요 기능

- **게시글** CRUD (작성·조회·수정·삭제)
- **댓글** 작성·삭제 (게시글과 연관관계)
- **검색·페이징** 제목 다중 단어 AND 검색 + 페이지네이션
- **회원** 회원가입(BCrypt 해싱), 로그인(세션 기반)
- **인가** 로그인 사용자만 작성 가능, 본인 글/댓글만 수정·삭제

---

## 기술적 고민과 해결

### N+1 문제 — "해결"이 아니라 "발생 원인 제거"

**문제**
게시글 목록에서 각 글의 댓글 수를 함께 보여주려 하자, 목록 조회 1회 뒤에
각 게시글의 댓글을 가져오는 쿼리가 게시글 수만큼 추가로 실행되었습니다. (`1 + N`)

```
select ... from post ...            -- 게시글 목록 1회
select ... from comment where post_id=?  -- 게시글마다 반복 (N회)
```

**원인 분석**
`@OneToMany` 연관을 지연 로딩하면서, 화면에서 `post.comments`에 접근하는 순간
각 게시글이 개별적으로 댓글을 조회했습니다.
흔한 해결책인 `fetch join`은 컬렉션 + 페이징을 함께 쓸 수 없고(메모리 페이징 발생),
`@BatchSize`는 쿼리 수는 줄여도 **실제로는 필요 없는 댓글 엔티티를 전부 로딩**한다는 한계가 있었습니다.

**해결**
목록 화면이 필요로 하는 것은 댓글 **내용이 아니라 개수**라는 점에 주목해,
연관 컬렉션을 아예 로딩하지 않고 **서브쿼리로 개수만 조회**하도록 변경했습니다.
QueryDSL로 게시글 조회 시 작성자는 `JOIN`, 댓글 수는 상관 서브쿼리로 함께 가져옵니다.

```
select p.id, p.title, u.nickname, p.created_at,
       (select count(c.id) from comment c where c.post_id = p.id)
from post p join users u on u.id = p.user_id
order by p.id desc limit ?, ?
```

**결과**
게시글이 몇 개든 댓글 관련 추가 쿼리는 **0회**. N+1이 "해결"된 것이 아니라
**발생할 이유 자체가 사라졌습니다.**
(상세 페이지처럼 댓글 내용이 실제로 필요한 곳에서는 정상적으로 조회하도록 구분했습니다.)

---

### 그 외 고민들 (간략)

- **동적 검색 (QueryDSL)** — 다중 단어 AND 검색은 조건 개수가 런타임에 달라져
  정적 JPQL로 표현할 수 없어, `BooleanBuilder`로 조건을 동적 조립했습니다.
  도입 과정에서 QueryDSL 원본 라이브러리의 취약점(CVE-2024-49203)을 확인하고,
  영향 범위를 분석한 뒤 패치된 포크로 이전했습니다.

- **소유권 기반 인가** — "본인 글만 수정"을 화면에서 버튼을 숨기는 것만으로는
  URL 직접 요청에 취약하므로, **서버 계층에서 작성자와 요청자를 대조**해 이중으로 방어했습니다.

- **계층 분리 & DTO** — Entity를 화면에 직접 노출하지 않고 용도별 DTO로 분리했으며,
  트랜잭션 경계 밖에서의 지연 로딩 예외를 방지하기 위해 서비스 계층에서 DTO로 변환합니다.

- **테스트** — 서비스 로직은 Mockito 단위 테스트로, QueryDSL 쿼리는 `@DataJpaTest` 통합
  테스트로 검증했습니다. (검색 조건 필터링·댓글 수 집계 정확성 포함)

---

## 실행 방법

### 사전 준비
- Java 17+
- MySQL

### 1. DB 생성
```sql
CREATE DATABASE board CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

### 2. 환경변수 설정
민감 정보는 코드에 포함하지 않고 환경변수로 주입합니다.

```bash
export DB_USERNAME=root
export DB_PASSWORD=your_password
```

### 3. 실행
```bash
./gradlew bootRun
```
`http://localhost:8080/posts` 접속.

---

## 프로젝트 구조

```
com.example.board
- config       # QuerydslConfig, SecurityConfig
- domain       # Post, Comment, User, BaseTimeEntity
- repository    # JpaRepository + QueryDSL 커스텀
- service       # 비즈니스 로직
- controller    # 요청 처리
- dto           # 요청/응답 객체
- exception     # 전역 예외 처리
- aop           # 실행 로깅
```


---
