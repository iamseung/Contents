# 작품 API 개발

## 📃 API 명세서
보다 자세한 사항과 테스트는 Swagger 를 통해서 확인하실 수 있습니다.

# 📘 API 명세서

---

## ✨ 유저 (User)
| HTTP Method | Endpoint             | 설명     |
|-------------|----------------------|----------|
| `POST`      | `/api/users/signup`  | 회원가입 |
| `POST`      | `/api/users/login`   | 로그인   |

---

## 🎬 작품 (Content)
| HTTP Method | Endpoint                              | 설명       |
|-------------|----------------------------------------|------------|
| `POST`      | `/api/content/{contentId}/view`        | 작품 조회  |
| `POST`      | `/api/content/{contentId}/purchase`    | 작품 구매  |
| `DELETE`    | `/api/content/{contentId}`             | 작품 삭제  |

---

## 👀 조회 이력 (View History)
| HTTP Method | Endpoint                                     | 설명                      |
|-------------|----------------------------------------------|---------------------------|
| `GET`       | `/api/view-history/{contentId}`            | 특정 작품의 조회 이력 조회 |
| `GET`       | `/api/view-history/popular-content-top10`    | 인기 조회 작품 Top 10 조회 |
| `DELETE`    | `/api/view-history/{viewHistoryId}`          | 조회 이력 단건 삭제        |

---

## 💳 구매 이력 (Purchase History)
| HTTP Method | Endpoint                                            | 설명                      |
|-------------|-----------------------------------------------------|---------------------------|
| `GET`       | `/api/purchase-history/popular-content-top10`      | 인기 구매 작품 Top 10 조회 |
| `DELETE`    | `/api/purchase-history/{purchaseHistoryId}`        | 구매 이력 단건 삭제        |
---

## 🌳 코드 구조

```
├── ContentsApplication.java
├── component
│   └── ContentEventListener.java
├── config
│   ├── CacheConfig.java
│   ├── LocalCacheConfig.java
│   ├── SecurityConfig.java
│   └── SwaggerConfig.java
├── controller
│   ├── ContentController.java
│   ├── PurchaseHistoryController.java
│   ├── UserController.java
│   └── ViewHistoryController.java
├── domain
│   ├── dto
│   │   ├── ApiSuccessResponse.java
│   │   ├── ContentDeleteEvent.java
│   │   ├── CursorPaginationResponse.java
│   │   ├── CustomUserDetails.java
│   │   ├── ErrorResponseDto.java
│   │   ├── LoginRequestDto.java
│   │   ├── PopularPurchaseContentResponse.java
│   │   ├── PopularViewContentResponse.java
│   │   ├── SignUpRequestDto.java
│   │   ├── UserDto.java
│   │   └── ViewHistoryResponse.java
│   └── entity
│       ├── Content.java
│       ├── ContentRedisEntity.java
│       ├── PurchaseHistory.java
│       ├── User.java
│       └── ViewHistory.java
├── exception
│   ├── BaseException.java
│   └── ErrorCode.java
├── handler
│   └── GlobalExceptionHandler.java
├── jwt
│   ├── JwtAuthenticationFilter.java
│   └── JwtUtil.java
├── repository
│   ├── ContentRepository.java
│   ├── PurchaseHistoryRepository.java
│   ├── UserRepository.java
│   └── ViewHistoryRepository.java
└── service
    ├── ContentCacheService.java
    ├── ContentService.java
    ├── CustomUserDetailsService.java
    ├── PurchaseHistoryService.java
    ├── RankingCacheService.java
    ├── UserService.java
    └── ViewHistoryService.java
```

# ✨ 주요 기능 및 설계

## 1. 도메인 선정 및 설계 배경
이 프로젝트는 작품, 사용자, 구매 이력, 조회 이력으로 구성된 웹툰 서비스를 다루며, 실제 서비스에서 발생할 수 있는 아래의 시나리오를 기반으로 도메인 구조를 설계했습니다.
<br> 기본적으로 `사용자 인증`을 기반으로 `성인 작품 구매 및 조회에 대한 비즈니스 로직 또한 구현`되어 있습니다.

### 핵심 도메인 설계

#### 📘 Content (작품)

- **id**: 작품 고유 ID (PK)
- **title**: 작품 제목
- **description**: 작품 설명
- **isAdult**: 성인용 콘텐츠 여부 (true 시 성인만 접근 가능)
- **isFree**: 무료 콘텐츠 여부
- **eventStartAt**: 이벤트 시작 시각 (기간 내에는 무료로 전환됨)
- **eventEndAt**: 이벤트 종료 시각
- **createdAt**: 등록 시각

> `isFree()` 메서드를 통해 이벤트 기간에 따라 무료 여부를 동적으로 판단합니다.
> <br>`isCanAccess(User)` 메서드는 성인 여부를 기반으로 접근 가능 여부를 판단합니다.

**✅ 작품이 삭제될 때 고아 객체 삭제를 고려할 수도 있었지만, 개별 DELETE 쿼리가 발생할 수 있습니다. 그러므로 contentID 기준으로 일괄 삭제하는 메서드를 추가로 생성하고, 연관 관계를 따로 지정하지 않았습니다.**

---

#### 💰 PurchaseHistory (구매 이력)

- **id**: 구매 이력 ID (PK)
- **user**: 구매자 (User, ManyToOne)
- **content**: 구매한 콘텐츠 (Content, ManyToOne)
- **purchasedAt**: 구매 시각

> 유저와 콘텐츠 간 다대다(N:M) 관계를 해소하는 엔티티입니다.

---

#### 👀 ViewHistory (조회 이력)

- **id**: 조회 이력 ID (PK)
- **user**: 조회한 사용자 (User, ManyToOne)
- **content**: 조회한 콘텐츠 (Content, ManyToOne)
- **viewedAt**: 조회 시각

> 사용자의 콘텐츠 열람 정보를 기록합니다.

---

## 2. 대용량 트래픽에 대비한 캐싱 전략

서비스가 성장함에 따라 트래픽 증가로 인한 DB 부하와 응답 속도 저하는 필연적인 문제입니다. 이를 해결하기 위해 본 프로젝트에서는 다음과 같은 **캐싱 전략**을 적용하여 성능과 안정성을 확보했습니다.

### ⭐️ 멀티 레벨 캐시 전략: Redis + Local Cache 병행 설계

실제 서비스 환경에서는 사용자 요청이 수천, 수만 건 단위로 발생하며, 특정 콘텐츠(작품) 또는 인기 랭킹과 같은 데이터는 반복적으로 조회됩니다.

이런 요청을 모두 DB로 처리한다면 RDS에 과부하가 발생하게 되며, 단순한 Redis 캐시만으로는 네트워크 레이턴시를 완전히 줄일 수 없습니다.

*`Redis + LocalCache`* 를 함께 사용한 이유는 단순히 빠른 응답을 넘어서, 대용량 트래픽에서의 DB 보호, 네트워크 병목 해소, 사용자 경험 향상 이라는 종합적인 목적을 달성하기 위함입니다.

서비스 특성에 따라 TTL 설정, 캐시 무효화 전략 등을 유연하게 조절할 수 있도록 구성했습니다.

---

### ✅ 해결 전략: **2단계 캐시(Multi-level Cache)**

```txt
1차 캐시 → Local Cache (Caffeine)
2차 캐시 → Redis Cache
3차 → RDB (Fallback)
```
---

### 1. 작품(Content) 캐싱

#### ✅ 캐싱 목적
- 콘텐츠 상세 정보를 매번 DB에서 조회하면 조회량 증가에 따라 병목 발생
- 특히 유료/무료, 이벤트 기간 등 조건이 있는 필드에 대해 반복 조회 시 부하 심화
- `ContentRedisEntity` 라는 도메인 모델을 통해서 성인 작품 유무, 유료/무료 판별 등의 `도메인 로직` 구현

#### ✅ 로컬 캐시 병행
- Hot data(자주 조회되는 콘텐츠)에 대해서는 **localCacheManager** 기반의 **로컬 캐시**도 구성
- WAS 내부에서 빠르게 조회 가능하여 Redis RoundTrip 조차 발생하지 않도록 최적화

#### ✅ 캐시 무효화
- 작품 삭제 시 `@TransactionalEventListener(phase = AFTER_COMMIT)` 이벤트를 통해 Redis + Local 캐시 모두 제거
- 무료/유료 상태 변경 시 `@CachePut`을 통해 갱신

---

### 2. 인기 조회/구매 Top 10 캐싱

#### ✅ 캐싱 목적
- 인기 콘텐츠 목록은 방문자 수가 많을수록 조회 빈도가 급증
- 단순히 정렬과 COUNT를 위한 쿼리조차 트래픽 증가 시 DB에 큰 부하 초래

#### ✅ 구현 방식
- 인기 조회 Top 10 → `view_history` 테이블의 `COUNT` 기반
- 인기 구매 Top 10 → `purchase_history` 테이블의 `COUNT` 기반
- 각각 별도의 `@Cacheable` 설정으로 Redis, LocalCache 에 캐싱

> 해당 API 의 캐시 무효화 정책은 부서마다 상이할 수 있다고 판단하여(구매 및 조회가 일정 수치를 넘기는 등) 따로 정책을 지정하진 않았습니다.

## 3. 대용량 데이터에 대비한 페이지 네이션 도입
작품 상세 조회 시, 해당 작품의 조회 이력을 커서 기반으로 조회할 수 있도록 API를 구현했습니다. 페이지네이션 방식으로는 오프셋 기반보다 커서 기반이 성능상 유리하기 때문에, 트래픽이 많은 환경에서도 안정적인 응답 속도를 유지할 수 있도록 설계했습니다.

---
Controller에서는 작품 ID를 받아오고, 커서 ID와 페이지 크기를 파라미터로 받습니다.
Service 단에서는 먼저 Content ID에 대해 Redis local cache에서 작품 정보를 조회합니다. 이로써 DB 호출을 최소화했습니다.

```java
ContentRedisEntity content = contentCacheService.getContentLocalCache(contentId);
```

결과로 내려주는 CursorPaginationResponse 안에는 다음 페이지를 위한 커서값인 nextCursorId도 포함되어 있습니다.
<br> 현재 조회된 결과가 있다면 가장 마지막 요소의 ID를 nextCursorId로 사용하고, 만약 결과가 없다면 더 이상 다음 페이지가 없다는 뜻으로 null을 내려줍니다.

이 방식을 적용함으로써 클라이언트에서는 nextCursorId 만 넘겨주면 다음 페이지를 손쉽게 이어받을 수 있고, 서버는 고정된 index가 아닌 ID 기반으로 빠르게 데이터를 검색할 수 있어 속도 저하 없이 무한 스크롤이 가능해집니다.

> 1. 오프셋 기반에 비해 커서 기반은 LIMIT + WHERE > id 쿼리로 처리되기 때문에 레코드 수가 많을수록 성능 차이가 커질 수 있습니다.
> 2. Redis local cache를 통해 콘텐츠 정보 조회 시 DB 조회를 줄이고 응답 시간 개선


## 4. 코드 품질 분석
유지보수성과 확장성을 고려하여 코드 품질을 매우 중요하게 생각했습니다. 다음과 같은 도구 및 기준을 통해 코드 품질을 지속적으로 개선했습니다.

### ✅ Code Metrics 기반의 복잡도 관리
- 순환 복잡도(Cyclomatic Complexity) 분석을 통해 비즈니스 로직의 복잡도를 정량적으로 측정하고 관리했습니다.
- 가능한 한 단일 책임 원칙(SRP) 을 지키며, 하나의 메서드가 한 가지 일만 수행하도록 리팩토링을 반복했습니다.

### ✅ SonarQube를 통한 정적 코드 분석
- SonarQube를 통해 Code Smell, Bug, Coverage, Duplications 등 다양한 항목에 대해 검증을 진행했습니다.
- 특히 중복 코드 제거, 미사용 코드 제거 등의 지표를 개선하며 신뢰도와 유지보수성을 향상시켰습니다.

---

## 5. 빌드 및 실행 방법
- docker 파일을 실행해 주세요.
- 기능 테스트와 명세는 Swagger 를 통해서 확인할 수 있습니다.