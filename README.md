#  HotDeal - 실시간 핫딜 이벤트 시스템

---

## 목차

* [프로젝트 개요](#프로젝트-개요)
* [아키텍처 및 설계](#아키텍처-및-설계)

  * [도메인 주도 설계 (DDD)](#도메인-주도-설계-ddd)
  * [HTTP 통신 방식](#http-통신-방식)
  * [실시간 알림 구조](#실시간-알림-구조)
  * [데이터 일관성 전략](#데이터-일관성-전략)
* [데이터 흐름도](#데이터-흐름도)
* [ERD](#erd)
* [주요 기능](#주요-기능)
* [기술 스택](#기술-스택)
* [프로젝트 구조](#프로젝트-구조)
* [API 명세](#api-명세)
* [실행 방법](#실행-방법)
* [환경 설정](#환경-설정)
* [주요 비즈니스 로직](#주요-비즈니스-로직)
* [트러블 슈팅](#트러블-슈팅)
* [관련 블로그](#관련-블로그)

---


# 프로젝트 개요

**HotDeal**은 실시간 할인 이벤트 관리 및 주문 처리를 위한 시스템입니다. 

* 동시성 제어를 위한 **Redisson 기반 분산 락**
* WebSocket을 활용한 실시간 이벤트 알림
* 도메인 간 분리와 향후 확장성을 고려해 내부 API 통신 방식 적용

---

# 아키텍처 및 설계

## 도메인 주도 설계 (DDD)
이 프로젝트는 복잡한 비즈니스 로직을 명확하게 구조화하기 위해
**도메인 주도 설계(DDD)**를 기반으로 전체 아키텍처를 구성했습니다.

특히 가장 많은 시간과 고민을 투자한 부분은
도메인 간의 책임을 구분하고 그 경계를 명확히 정의하는 것이었습니다.

---

### 도메인 주도 설계를 선택한 이유
이벤트, 상품, 주문과 같은 도메인은 각각 독립적인 비즈니스 개념을 가지고 있으며
이들이 교차하는 지점에서는 데이터 의존성과 변경 전파의 위험이 항상 존재합니다.

이에 따라 우리는 **도메인 주도 설계(DDD)** 를 선택하였고 이를 통해 다음과 같은 효과를 얻고자 했습니다.

1. 분리된 개발 환경 구축
2. 도메인 간 책임과 역할 명확화
3. 불필요한 변경 전파 차단
4. 비즈니스 규칙과 흐름이 반영된 모델링
5. 현실 업무와 유사한 구조로 협업 효율 향상

---
## HTTP 통신 방식
이 프로젝트에서는 서버 간 통신을 위해 Spring의 `RestTemplate`을 선택했습니다.

비록 RestTemplate은 레거시로 분류되며 WebClient가 공식적으로 권장되는 추세이지만
이번 프로젝트에서는 다음과 같은 이유로 RestTemplate을 우선 도입을 결정했습니다.

`RestTemplate`은 동기/블로킹 기반으로 동작하기 때문에 전체 흐름을 직관적으로 파악할 수 있어
디버깅 및 문제 추적이 상대적으로 수월합니다.

`WebClient`는 비동기/논블로킹 기반으로 고성능 처리에 적합하지만 초기 학습 비용이 크고
예외 처리나 장애 추적에 대한 진입 장벽이 높을 수 있습니다.

프로젝트 초반에는 핵심 도메인 설계 및 흐름 파악에 집중을 위해 통신 방식에 대한 복잡도는 최소화하고자 했습니다.
향후 확장 또는 성능 최적화가 필요한 구간에서는 `WebClient`로의 전환을 고려할 수 있도록 유연한 구조로 설계하였습니다.

---

## 실시간 알림 구조
이 프로젝트에서는 대량 사용자에게 안정적으로 알림을 전송할 수 있는 구조를 목표로
실시간 알림 시스템을 설계했습니다.

초기에는 서버가 제품을 구독 중인 모든 사용자에게 웹소켓을 통해 직접 알림을 푸시하는 방식이었고
이는 소규모 트래픽 환경에서는 잘 동작했습니다.
그러나 구독자가 많아지는 상황에서는 다음과 같은 문제점이 발생할 수 있음을 고려했습니다.

- 연결 풀(pool) 한계로 인해 동시 연결 수 제한
- 동기 처리 지연으로 인한 응답 속도 저하
- 메모리 사용량 증가 및 GC 부하
- 구독자 목록 조회 시점의 성능 저하

---

### 설계 방향과 구조적 대응
이러한 문제를 해결하기 위해 다음과 같은 전략을 설계하고 반영했습니다.

DB 측면에서는 인덱싱과 캐싱을 통해 조회 성능을 확보했고
웹소켓 처리 로직은 배치 전송 및 병렬 처리로 부담을 분산했습니다.

하지만 웹소켓 연결 수 제한은 애플리케이션 차원에서 근본적으로 해결할 수 없는 구조적 제약이었습니다.
이에 따라 실시간 알림 구조를 클라이언트 주도 방식으로 전환하는 방향으로 재설계했습니다.

---

### 최종 설계 방향
최종적으로는 다음과 같은 방식으로 알림 시스템을 구성했습니다.

- 서버는 “제품에 새로운 이벤트가 등록되었음을 알리는 신호”만 전송
- 클라이언트는 해당 신호를 수신한 뒤 필요한 알림 데이터를 직접 조회

이러한 구조는 다음과 같은 장점을 가집니다.

- 서버가 모든 구독자에게 직접 데이터를 전송하지 않기 때문에 웹소켓 연결 수 제한 문제를 효과적으로 회피
- 클라이언트가 필요한 시점에 필요한 데이터만 요청하여 리소스 효율성 상승
- 전체 시스템의 확장성과 안정성 향상

---
## 데이터 일관성 전략

### 아키텍처 구조 및 데이터 흐름
이 프로젝트는 인증 정보(Auth)와 사용자 프로필(User)을 서로 다른 목적과 책임을 가진 테이블로 분리하여 관리합니다.
Auth는 사용자 인증에 필요한 정보를 관리하고 User는 사용자 조회 전용 데이터를 제공하는 Read Model 역할을 수행합니다.

두 테이블은 도메인 이벤트 기반으로 연동되며 예를 들어 회원가입 시 Auth 저장 이후 `UserRegisteredEvent`가 발행을 통해
이를 구독하는 리스너가 User 데이터를 생성합니다.

---

### 이벤트 기반 + 보상 트랜잭션 설계
이벤트 기반 아키텍처는 비동기적이며 유연하지만 비정상적인 흐름(예: 리스너 실패)으로 인해 일부 데이터가
누락되거나 일관성이 어긋날 가능성도 존재합니다.

이를 보완하기 위해 보상 트랜잭션 패턴을 아키텍처에 포함시켰습니다.

User 저장 리스너에서 예외가 발생할 경우 `UserSaveFailedEvent`를 발행해 Auth 데이터를 되돌리는 보상 로직을 실행합니다.

---


## 데이터 흐름도

<img width="1223" height="817" alt="image" src="https://github.com/user-attachments/assets/2a29716d-f63c-4937-ac6a-f0a1ef5bdd5e" />


## ERD

<img width="3054" height="3758" alt="image" src="https://github.com/user-attachments/assets/769dff61-2482-4efb-a682-174d5a506e60" />


---

## 주요 기능
### 1. 이벤트 관리
- 할인 이벤트 생성 및 관리
- 상품별 최적 할인율 자동 계산
- 만료된 이벤트 자동 삭제 (스케줄러)

### 2. 주문 처리
- 다중 상품 주문 지원
- 이벤트 할인가 자동 적용
- 주문 상태 관리 (ORDER_BEFORE, ORDER_PENDING, ORDER_SUCCESS, ORDER_FAILURE)

### 3. 재고 관리
- Redisson을 활용한 분산 락 구현
- 동시성 제어를 통한 재고 차감
- 재고 부족 시 자동 주문 실패 처리

### 4. 실시간 알림
- WebSocket을 통한 실시간 이벤트 알림
- Spring Event를 활용한 비동기 처리

## 기술 스택
### Backend
- Java 17
- Spring Boot 3.5.3
- Spring Data JPA
- Spring Security
- Spring WebSocket

### Database
- MySQL
- Redis 


### 기타
- Redisson
- JWT
- Lombok
- TestContainers


## 프로젝트 구조
<details>
<summary><b>프로젝트 구조</b></summary>
    
```
com.example.hotdeal
├── domain
│   ├── common
│   │   ├── client           # 내부 API 클라이언트
│   │   │   ├── event
│   │   │   ├── product
│   │   │   └── stock
│   │   └── springEvent      # Spring Event 정의
│   ├── event               # 이벤트 도메인
│   │   ├── api
│   │   ├── application
│   │   ├── domain
│   │   └── infra
│   ├── order               # 주문 도메인
│   │   ├── api
│   │   ├── application
│   │   ├── domain
│   │   └── infra
│   ├── stock               # 재고 도메인
│   │   ├── api
│   │   ├── application
│   │   ├── domain
│   │   └── infra
│   ├── notification        # 알림 도메인
│   │   ├── application
│   │   ├── domain
│   │   └── infra
│   └── user               # 사용자 도메인
│       └── auth
└── global
    ├── config              # 전역 설정
    ├── enums              # 공통 열거형
    ├── exception          # 예외 처리
    ├── lock               # 분산 락 구현
    └── model              # 공통 모델
```
</details>


## API 명세

<details>
<summary><b>전체 API 개요</b></summary>

### 인증 (Auth) API
| API | Method | Endpoint | 권한 | 설명 |
|-----|--------|----------|------|------|
| 회원가입 | POST | `/api/auth/signup` | PUBLIC | 새로운 사용자 회원가입 |
| 로그인 | POST | `/api/auth/login` | PUBLIC | 사용자 로그인 및 토큰 발급 |
| 토큰 재발급 | POST | `/api/auth/reissue` | PUBLIC | Access Token 재발급 |
| 로그아웃 | POST | `/api/auth/logout` | USER | 사용자 로그아웃 |
| 회원탈퇴 | POST | `/api/auth/withdraw` | USER | 사용자 계정 비활성화 |
| 계정 복구 | POST | `/api/auth/{authId}/restore` | ADMIN | 탈퇴한 계정 복구 |

### 사용자 (User) API
| API | Method | Endpoint | 권한 | 설명 |
|-----|--------|----------|------|------|
| 내 정보 조회 | GET | `/api/users/me` | USER | 현재 로그인한 사용자 정보 조회 |

### 상품 (Product) API
| API | Method | Endpoint | 권한 | 설명 |
|-----|--------|----------|------|------|
| 상품 목록 조회 | POST | `/api/products/search-product` | USER | 여러 상품 정보를 한 번에 조회 |
| 단일 상품 조회 | GET | `/api/products/{productId}` | USER | 특정 상품의 상세 정보 조회 |
| 상품 생성 | POST | `/api/products` | ADMIN | 새로운 상품 등록 |
| 상품 수정 | PUT | `/api/products/{productId}` | ADMIN | 기존 상품 정보 수정 |
| 상품 삭제 | DELETE | `/api/products/{productId}` | ADMIN | 상품 소프트 삭제 |

### 재고 (Stock) API
| API | Method | Endpoint | 권한 | 설명 |
|-----|--------|----------|------|------|
| 재고 목록 조회 | POST | `/api/stocks/search` | USER | 여러 상품의 재고 조회 |
| 단일 재고 조회 | GET | `/api/stocks/product/{productId}` | USER | 특정 상품의 재고 조회 |
| 재고 증가 | POST | `/api/stocks/product/{productId}/increase` | ADMIN | 상품 재고 증가 |
| 재고 초기화 | POST | `/api/stocks/product/{productId}/reset` | ADMIN | 상품 재고 초기화 |

### 이벤트 (Event) API
| API | Method | Endpoint | 권한 | 설명 |
|-----|--------|----------|------|------|
| 이벤트 생성 | POST | `/api/event/create` | ADMIN | 새로운 핫딜 이벤트 생성 |
| 이벤트 조회 | POST | `/api/event/search-event` | USER | 상품별 이벤트 정보 조회 |

### 주문 (Order) API
| API | Method | Endpoint | 권한 | 설명 |
|-----|--------|----------|------|------|
| 다중 상품 주문 | POST | `/api/orders/products` | USER | 다중 상품 주문 (RestTemplate 방식) |
| 주문 취소 | PUT | `/api/orders/{orderId}` | USER | 기존 주문 취소 |
| 주문 조회 | GET | `/api/orders/{orderId}` | USER | 주문 상세 정보 조회 |

### 구독 (Subscribe) API
| API | Method | Endpoint | 권한 | 설명 |
|-----|--------|----------|------|------|
| 상품 구독 | POST | `/api/subscribe/sub-product` | USER | 상품 알림 구독 등록 |
| 구독자 조회 | GET | `/api/subscribe/search-sub-user` | USER | 특정 상품 구독자 목록 조회 |
| 구독 취소 | DELETE | `/api/subscribe/cancel-sub` | USER | 상품 구독 취소 |

</details>



<details>
<summary><b>API 상세 정보</b></summary>

## 인증 (Auth) API

### 1. 회원가입
```
POST /api/auth/signup
```

<details>
<summary><b>Request Body</b></summary>

```json
{
  "email": "user@example.com",
  "name": "홍길동",
  "password": "password123"
}
```

</details>

### 2. 로그인
```
POST /api/auth/login
```

<details>
<summary><b>Request Body</b></summary>

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

</details>

### 3. 토큰 재발급
```
POST /api/auth/reissue
```

<details>
<summary><b>Request Body</b></summary>

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

</details>

### 4. 로그아웃
```
POST /api/auth/logout
Authorization: Bearer {token}
```

<details>
<summary><b>Request Body</b></summary>

```json
{
  "password": "password123"
}
```

</details>

### 5. 회원탈퇴
```
POST /api/auth/withdraw
Authorization: Bearer {token}
```

<details>
<summary><b>Request Body</b></summary>

```json
{
  "password": "password123"
}
```

</details>

### 6. 계정 복구 
```
POST /api/auth/{authId}/restore
Authorization: Bearer {token} (ADMIN 권한 필요)
```

<details>
<summary><b>Query Parameter</b></summary>

- `authId`: 복구할 사용자 ID

</details>

---

## 사용자 (User) API

### 1. 내 정보 조회
```
GET /api/users/me
Authorization: Bearer {token}
```

---

##  상품 (Product) API

### 1. 상품 목록 조회
```
POST /api/products/search-product
Authorization: Bearer {token}
```

<details>
<summary><b>Request Body</b></summary>

```json
{
  "productIds": [1, 2, 3, 4, 5]
}
```

</details>

### 2. 단일 상품 조회
```
GET /api/products/{productId}
Authorization: Bearer {token}
```

### 3. 상품 생성 
```
POST /api/products
Authorization: Bearer {token} (ADMIN 권한 필요)
```

<details>
<summary><b>Request Body</b></summary>

```json
{
  "productName": "Galaxy S24 Ultra",
  "productDescription": "삼성 최신 플래그십 스마트폰",
  "productPrice": 1600000,
  "productImageUrl": "https://example.com/galaxy-s24.jpg",
  "productCategory": "ELECTRONICS"
}
```

</details>

### 4. 상품 수정 
```
PUT /api/products/{productId}
Authorization: Bearer {token} (ADMIN 권한 필요)
```

<details>
<summary><b>Request Body</b></summary>

```json
{
  "productName": "iPhone 15 Pro Max 업데이트",
  "productDescription": "업데이트된 상품 설명",
  "productPrice": 1700000,
  "productImageUrl": "https://example.com/updated-iphone15.jpg",
  "productCategory": "ELECTRONICS"
}
```

</details>

### 5. 상품 삭제 
```
DELETE /api/products/{productId}
Authorization: Bearer {token} (ADMIN 권한 필요)
```

---

##  재고 (Stock) API

### 1. 재고 목록 조회
```
POST /api/stocks/search
Authorization: Bearer {token}
```

<details>
<summary><b>Request Body</b></summary>

```json
{
  "productIds": [1, 2, 3, 4, 5]
}
```

</details>

### 2. 단일 재고 조회
```
GET /api/stocks/product/{productId}
Authorization: Bearer {token}
```

### 3. 재고 증가 
```
POST /api/stocks/product/{productId}/increase?quantity={수량}
Authorization: Bearer {token} (ADMIN 권한 필요)
```

### 4. 재고 초기화 
```
POST /api/stocks/product/{productId}/reset?quantity={수량}
Authorization: Bearer {token} (ADMIN 권한 필요)
```

---

##  이벤트 (Event) API

### 1. 이벤트 생성 
```
POST /api/event/create
Authorization: Bearer {token} (ADMIN 권한 필요)
```

<details>
<summary><b>Request Body</b></summary>

```json
{
  "eventType": "HOT_DEAL",
  "eventDiscount": 20,
  "eventDuration": 7,
  "startEventTime": "2025-07-15T00:00:00",
  "productIds": [1, 2, 3, 4, 5]
}
```

</details>

### 2. 이벤트 조회
```
POST /api/event/search-event
Authorization: Bearer {token}
```

<details>
<summary><b>Request Body</b></summary>

```json
{
  "productIds": [1, 2, 3, 4, 5]
}
```

</details>

---

##  주문 (Order) API

### 1. 단일 상품 주문
```
POST /api/orders/v1
Authorization: Bearer {token}
```

<details>
<summary><b>Request Body</b></summary>

```json
{
  "productId": 1,
  "quantity": 2
}
```

</details>

### 2. 다중 상품 주문
```
POST /api/orders/v2
Authorization: Bearer {token}
```

<details>
<summary><b>Request Body</b></summary>

```json
{
  "orderItems": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 1
    }
  ]
}
```

</details>

### 3. 주문 취소
```
PUT /api/orders/{orderId}
Authorization: Bearer {token}
```

### 4. 주문 조회
```
GET /api/orders/{orderId}
Authorization: Bearer {token}
```

---

##  구독 (Subscribe) API

### 1. 상품 구독
```
POST /api/subscribe/sub-product
Authorization: Bearer {token}
```

<details>
<summary><b>Request Body</b></summary>

```json
{
  "productIds": [1, 2, 3, 4, 5]
}
```

</details>

### 2. 구독자 조회
```
GET /api/subscribe/search-sub-user?productId={상품ID}
Authorization: Bearer {token}
```

### 3. 구독 취소
```
DELETE /api/subscribe/cancel-sub?userId={사용자ID}&productId={상품ID}
Authorization: Bearer {token}
```
</details>

<details>
<summary><b>추가 정보</b></summary>

### 상품 카테고리


| 코드 | 한글명 | 코드 | 한글명 |
|------|--------|------|--------|
| `ELECTRONICS` | 전자제품 | `HEALTH` | 건강/의료 |
| `FASHION` | 패션/의류 | `BABY` | 육아/출산 |
| `BEAUTY` | 뷰티/화장품 | `PET` | 반려동물 |
| `HOME_LIVING` | 홈/리빙 | `CAR` | 자동차/용품 |
| `FOOD` | 식품 | `HOBBY` | 취미/수집 |
| `SPORTS` | 스포츠/레저 | `OFFICE` | 사무/문구 |
| `BOOKS` | 도서 | `OTHER` | 기타 |

</details>


## 실행 방법
<details>
<summary><b>실행 방법</b></summary>

### 1. 필수 요구사항
- JDK 17 이상
- MySQL 8.0
- Redis 6.0 이상
- Gradle 7.x 이상

### 2. 환경 준비

#### 방법 1: 로컬 설치
```bash
# MySQL과 Redis를 로컬에 직접 설치하여 사용
# MySQL: 3306 포트
# Redis: 6379 포트
```

#### 방법 2: Docker 사용 (권장)
```bash
# 1. 프로젝트 클론
git clone https://github.com/your-repo/hotdeal.git
cd hotdeal

# 2. .env 파일 생성
cp .env.example .env
# .env 파일을 열어 환경 변수 값 설정

# 3. Docker Compose로 인프라 실행
docker-compose up -d
```

### 3. 애플리케이션 실행
```bash
# 1. 환경 변수 설정 (터미널에서 실행하는 경우)
export DB_SCHEME=hotdeal
export DB_USERNAME=root
export DB_PASSWORD=your_password
export SECRET_KEY=your_secret_key_at_least_256_bits_long

# 2. Gradle 빌드
./gradlew clean build

# 3. 애플리케이션 실행
./gradlew bootRun

# 또는 JAR 파일로 실행
java -jar build/libs/hotdeal-0.0.1-SNAPSHOT.jar

# IDE(IntelliJ IDEA 등)에서 실행하는 경우
# Run Configuration에서 환경 변수 설정 후 실행
```

### 4. 실행 순서
1. **인프라 환경 준비**
   - MySQL 서버 시작 (3306 포트)
   - Redis 서버 시작 (6379 포트)

2. **환경 변수 설정**
   - `.env` 파일 생성 및 환경 변수 설정
   - 또는 IDE/터미널에서 환경 변수 export

3. **애플리케이션 실행**
   ```bash
   ./gradlew bootRun
   ```

4. **초기 데이터 설정**
   - 상품 데이터 초기화 (ProductDataInsertTest 활용 가능)
   - 재고 데이터 설정 (각 상품별 재고 API 호출)
   
5. **서비스 이용**
   - 이벤트 생성 (관리자)
   - WebSocket 연결 (실시간 알림 수신용)
   - 주문 처리

</details>


## 환경 설정
<details>
<summary><b>환경 설정</b></summary>
    
### .env 파일 (환경 변수)

<details>
<summary><b>.env 파일 예시 보기</b></summary>

```env
# Database
DB_SCHEME=hotdeal
DB_USERNAME=root
DB_PASSWORD=your_password

# JWT
SECRET_KEY=your_secret_key_at_least_256_bits_long

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
```
</details>

### application.yml

<details>
<summary><b>application.yml 전체 설정 보기</b></summary>

```yaml
spring:
  application:
    name: HotDeal

  datasource:
    url: jdbc:mysql://localhost:3306/${DB_SCHEME}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 500
      minimum-idle: 50
      connection-timeout: 60000
      idle-timeout: 600000
      max-lifetime: 1800000
      leak-detection-threshold: 60000

  jpa:
    hibernate:
      ddl-auto: create-drop  # 운영환경에서는 validate 또는 none 사용
    properties:
      hibernate:
        show_sql: true
        format_sql: true
        use_sql_comments: true
        dialect: org.hibernate.dialect.MySQLDialect
    open-in-view: false

  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}

jwt:
  secret:
    key: ${SECRET_KEY}

redis:
  host: ${REDIS_HOST:localhost}
  port: ${REDIS_PORT:6379}
  password: ${REDIS_PASSWORD:}

redisson:
  address: redis://${REDIS_HOST:localhost}:${REDIS_PORT:6379}
  password: ${REDIS_PASSWORD:}

server:
  port: 8080
```
</details>

### application-test.yml

<details>
<summary><b>application-test.yml 전체 설정 보기</b></summary>

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/test_db
    username: root
    password: 3030
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 30
      minimum-idle: 10

  data:
    redis:
      host: localhost
      port: 6379

  jpa:
    hibernate:
      ddl-auto: create-drop
    open-in-view: false

jwt:
  secret:
    key: dGVzdFNlY3JldEtleUZvclRlc3RpbmdQdXJwb3NlT25seTEyMzQ1Njc4OTAxMjM0NTY3ODkw
```
</details>

### docker-compose.yml (선택사항)
<details>
<summary><b>Docker를 사용하여 인프라를 구성하는 경우</b></summary>
    
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_PASSWORD}
      MYSQL_DATABASE: ${DB_SCHEME}
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      
  redis:
    image: redis:6.2-alpine
    ports:
      - "6379:6379"
    command: redis-server --requirepass ${REDIS_PASSWORD}
    volumes:
      - redis_data:/data

volumes:
  mysql_data:
  redis_data:
```
</details>

### 권한 레벨
- **PUBLIC**: 인증 없이 접근 가능
- **USER**: 일반 사용자 권한 필요
- **ADMIN**: 관리자 권한 필요

### 테스트 환경
- 테스트 실행 시 TestContainers가 자동으로 Redis 컨테이너를 생성
- 통합 테스트에서는 실제 MySQL과 TestContainers Redis를 함께 사용
- 테스트 프로파일(`@ActiveProfiles("test")`)로 별도 설정 적용
  
</details>


## 주요 비즈니스 로직

### 1. 동시성 제어 (재고 관리)
- **Redisson 분산 락**을 사용한 안전한 재고 차감
  - 락 획득 최대 대기시간: 4초
  - 락 유지 시간: 100ms
  - 동시 요청 시에도 정확한 재고 관리 보장
- **Redis 기반** 분산 환경 지원

### 2. 이벤트 처리 흐름
1. 이벤트 생성 시 WebSocket으로 실시간 알림 발송
2. Spring Event를 통한 비동기 처리
3. 스케줄러를 통한 만료 이벤트 자동 삭제 (매일 자정)
4. 상품별 최적 할인가 자동 계산 (동일 상품 다중 이벤트 시)

### 3. 주문 처리 흐름
1. 상품 정보 조회 (ProductApiClient)
2. 이벤트 할인가 조회 (HotDealApiClient)
3. 주문 생성 및 저장
4. OrderCreatedEvent 발행
5. 비동기로 재고 차감 처리 (StockEventListener)
6. 재고 부족 시 자동 롤백

### 4. 인증 및 보안
- JWT 토큰 기반 인증
- Redis를 활용한 토큰 블랙리스트 관리
- Spring Security 적용

### 5. 에러 처리
- CustomException을 통한 일관된 에러 처리
- 재고 부족, 상품 없음 등 비즈니스 예외 처리
- 트랜잭션 롤백 및 보상 처리
- Spring Event를 활용한 실패 시 보상 트랜잭션

---

# 트러블 슈팅

## 목차
1. [도메인 간 데이터 참조 방식 설계](#1-도메인-간-데이터-참조-방식-설계)
2. [내부 API 호출 시 인증 토큰 전달 문제](#2-내부-api-호출-시-인증-토큰-전달-문제)
3. [HTTP 클라이언트 선택](#3-http-클라이언트-선택-resttemplate-vs-webclient)
4. [Auth와 User 간의 데이터 일관성 문제](#4-auth와-user-간의-데이터-일관성-문제)
5. [웹소켓 알림 시스템 최적화](#5-웹소켓-알림-시스템-최적화)
6. [핫딜 이벤트 도메인 성능 최적화](#6-핫딜-이벤트-대용량-등록-시-성능-문제)

---

## 1. 도메인 간 데이터 참조 방식 설계

### 문제 상황
```
도메인 간의 경계를 명확히 하는 과정에서 발생한 핵심 딜레마
"주문 정보를 어떻게 구성할 것인가?"
```

<details>
<summary><b>고려한 방안들</b></summary>

| 방식 | 설명 | 장점 | 단점 |
|------|------|------|------|
| **방식 1** | 이벤트 아이템 테이블에서 모든 데이터 조회 | 단순한 조회 | 데이터 중복, 일관성 문제 |
| **방식 2** | 도메인별 책임 분리 (상품↔이벤트) | 일관성 보장, 도메인 독립성 | 복잡한 구현 |

</details>

### 선택한 해결 방안
**방식 2: 도메인별 책임 분리**

```
주문 도메인 → 상품 도메인 (기본 정보)
           → 이벤트 도메인 (할인 정보)
```

### 선택 이유
- **데이터 일관성 보장**: 상품 정보 변경 시 데이터 불일치 방지
- **도메인 독립성**: 각 도메인의 책임 범위 명확화 및 의존성 최소화

---

## 2. 내부 API 호출 시 인증 토큰 전달 문제

### 문제 상황
```
관리자 재고 증가 요청 → 내부 상품 검증 API 호출 → 401 인증 오류 발생
```

### 원인 분석
```
외부 요청: 인증됨 ✓
내부 API 호출: 토큰 정보 없음 ✗
```

### 해결 방안: RestTemplate 인터셉터

<details>
<summary><b>구현 코드</b></summary>

```java
@Bean
public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder
            .additionalInterceptors((request, body, execution) -> {
                // 현재 요청의 Authorization 헤더를 복사
                RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
                if (attrs instanceof ServletRequestAttributes) {
                    HttpServletRequest httpRequest = ((ServletRequestAttributes) attrs).getRequest();
                    String authHeader = httpRequest.getHeader("Authorization");
                    if (authHeader != null) {
                        request.getHeaders().set("Authorization", authHeader);
                        log.debug("JWT 토큰 전달 - URL: {}", request.getURI());
                    }
                }
                return execution.execute(request, body);
            })
            .build();
}
```

</details>

### 개선 효과
- 내부 API 호출 시 자동 토큰 전달
- 인증 관련 오류 완전 해결
- 추가 설정 없이 모든 RestTemplate 요청에 적용

---

## 3. HTTP 클라이언트 선택 (RestTemplate vs WebClient)

### 기술 선택 고민

| 기술 | 장점 | 단점 |
|------|------|------|
| **WebClient** | 비동기/논블로킹, 고성능 | 높은 학습 곡선, 복잡한 디버깅 |
| **RestTemplate** | 간단한 사용법, 쉬운 디버깅 | 동기 방식, 상대적 저성능 |

### 선택: RestTemplate

### 선택 이유
```
DDD 도메인 간 통신 구조를 잡아가는 상황에서는
빠른 문제 파악과 디버깅이 성능보다 우선순위가 높다고 판단
```

---

## 4. Auth와 User 간의 데이터 일관성 문제

### 문제 상황
```
회원가입 프로세스:
1. Auth 테이블에 데이터 저장 ✓
2. 이벤트 발행 ✓
3. User 테이블에 데이터 저장 ✗ (실패 가능)

결과: 데이터 일관성 깨짐
```

### 해결 방안: 보상 트랜잭션 (Saga Pattern)

<details>
<summary><b>구현 구조</b></summary>

```java
@EventListener
public void handlerUserRegisteredEvent(UserRegisteredEvent event) {
    try {
        User user = User.fromUserEvent(
            event.getUserId(), 
            event.getEmail(), 
            event.getName(), 
            event.getCreatedAt()
        );
        userRepository.save(user);
    } catch (Exception e) {
        // User 저장 실패 시 보상 트랜잭션 이벤트 발행
        eventPublisher.publishEvent(
            UserCreationFailedEvent.of(event.getUserId())
        );
    }
}
```

**보상 트랜잭션 흐름:**
```
User 저장 실패 → UserCreationFailedEvent 발행 → Auth 데이터 롤백
```

</details>

### 개선 효과
- 데이터 일관성 보장
- 분산 트랜잭션 환경에서의 안정성 확보
- 실패 상황에 대한 자동 복구 메커니즘

---

## 5. 웹소켓 알림 시스템 최적화

### 기존 방식의 문제점

#### 개별 전송 방식
```
서버 → 구독자 1
      → 구독자 2  
      → 구독자 3
      → ...
      → 구독자 N
```

#### 발생한 문제들

| 문제 | 설명 | 영향 |
|------|------|------|
| **연결 풀링 제한** | 동시 연결 수 한계 | 대용량 사용자 시 연결 끊김 |
| **동기 처리 지연** | 순차적 메시지 전송 | 마지막 사용자 알림 지연 |

### 고려한 해결 방안들
- 캐싱 적용
- 배치 처리
- **한계**: 웹소켓 연결 풀링 제한은 근본적 해결 불가

### 최종 해결 방안: 브로드캐스트 방식

#### 변경된 구조
```
서버: 공통 알림 브로드캐스트
클라이언트: 구독 상품 필터링 후 처리
```

<details>
<summary><b>구현 예시</b></summary>

**서버 측 (브로드캐스트)**
```java
@Service
public class NotificationService {
    public void notifyProductEvent(WSEventProduct event) {
        // 모든 연결된 클라이언트에게 브로드캐스트
        messagingTemplate.convertAndSend(
            "/topic/notification", 
            event.toNotificationMessage()
        );
    }
}
```

**클라이언트 측 (필터링)**
```javascript
stompClient.subscribe('/topic/notification', function(message) {
    const eventData = JSON.parse(message.body);
    
    // 사용자가 구독한 상품인지 확인
    if (userSubscribedProducts.includes(eventData.productId)) {
        displayNotification(eventData);
    }
});
```

</details>

### 개선 효과

| 개선 항목 | Before | After |
|-----------|--------|-------|
| **확장성** | 사용자 수에 비례한 성능 저하 | 사용자 수 무관한 일정 성능 |
| **처리 방식** | 동기 순차 처리 | 단일 브로드캐스트 |
| **연결 관리** | 개별 연결 관리 필요 | 단순한 연결 관리 |
| **알림 지연** | 마지막 사용자 지연 발생 | 모든 사용자 동시 수신 |


#### 발생한 문제들

| 문제 | 설명 | 영향 |
|------|------|------|
| **연결 풀링 제한** | 동시 연결 수 한계 | 대용량 사용자 시 연결 끊김 |
| **동기 처리 지연** | 순차적 메시지 전송 | 마지막 사용자 알림 지연 |

### 고려한 해결 방안들
- 캐싱 적용
- 배치 처리
- **한계**: 웹소켓 연결 풀링 제한은 근본적 해결 불가

### 최종 해결 방안: 브로드캐스트 방식

#### 변경된 구조
```
서버: 공통 알림 브로드캐스트
클라이언트: 구독 상품 필터링 후 처리
```

<details>
<summary><b>구현 예시</b></summary>

**서버 측 (브로드캐스트)**
```java
@Service
public class NotificationService {
    public void notifyProductEvent(WSEventProduct event) {
        // 모든 연결된 클라이언트에게 브로드캐스트
        messagingTemplate.convertAndSend(
            "/topic/notification", 
            event.toNotificationMessage()
        );
    }
}
```

**클라이언트 측 (필터링)**
```javascript
stompClient.subscribe('/topic/notification', function(message) {
    const eventData = JSON.parse(message.body);
    
    // 사용자가 구독한 상품인지 확인
    if (userSubscribedProducts.includes(eventData.productId)) {
        displayNotification(eventData);
    }
});
```

</details>

### 개선 효과

| 개선 항목 | Before | After |
|-----------|--------|-------|
| **확장성** | 사용자 수에 비례한 성능 저하 | 사용자 수 무관한 일정 성능 |
| **처리 방식** | 동기 순차 처리 | 단일 브로드캐스트 |
| **연결 관리** | 개별 연결 관리 필요 | 단순한 연결 관리 |
| **알림 지연** | 마지막 사용자 지연 발생 | 모든 사용자 동시 수신 |

## 6. 핫딜 이벤트 대용량 등록 시 성능 문제

### 성능 측정 결과 (개선 전)

```
1단계 - Event 저장 완료: 22ms
2단계 - ProductApiClient 호출 완료: 236ms (조회된 상품 수: 10000)
3단계 - EventItem 객체 생성 완료: 4ms
4단계 - EventItem insert 완료: 1052ms
5단계 - Event에 EventItem 리스트 설정 완료: 0ms
6단계 - WSEventProduct 객체 생성 완료: 1ms
7단계 - 이벤트 발행 완료: 799ms
=== createEvent 총 실행시간: 2115ms ===
8단계 - 컨트롤러 실행 완료: 4126ms
```

**문제점**:

-   컨트롤러 전체 실행 시간이 4126ms로 심각한 지연
-   EventItem 벌크 insert가 1052ms로 큰 병목
-   이벤트 리스너가 동기적으로 처리되어 지연 발생

### 해결 방안

#### 1\. Notification 벌크 인서트 적용
<details>
 <summary><b>구현 예시</b></summary>
 
 #### 개선 전: 개별 insert

```
// 기존 방식 - 개별 insert
notificationRepository.save(notification);
```

#### 개선 후: 벌크 insert

```
@Service
public class NotificationService {
    private final List<Notification> buffer = new ArrayList<>();

    public void addNotification(Notification notification) {
        synchronized (lock) {
            buffer.add(notification);
            if(buffer.size() >= 1000) {
                // 1000개씩 벌크 insert
                insertBatch();
            }
        }
    }

    @Async
    public void insertBatch() {
        List<Notification> notifications;
        synchronized (lock) {
            notifications = new ArrayList<>(buffer);
            buffer.clear();
        }
        notificationRepository.insertNotifications(notifications);
    }
}
```

**개선 효과**: 개별 insert → 벌크 insert로 변경하여 DB 호출 횟수 대폭 감소
</details>

#### 2\. EventItem 벌크 인서트 적용
<details>
 <summary><b>구현 예시</b></summary>
 
 #### 개선 전: JPA saveAll 사용

```
// 기존 방식
eventItemRepository.saveAll(eventItems);
```

#### 개선 후: JdbcTemplate batchUpdate 사용

```
@Repository
public class EventItemInsertRepository {

    public void insertEventItem(List<EventItem> eventItems, Long eventId) {
        String sql = "INSERT INTO event_items (event_id, product_id, product_name, original_price, discount_price, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, eventItems, 1000, (ps, eventItem) -> {
            ps.setLong(1, eventId);
            ps.setLong(2, eventItem.getProductId());
            ps.setString(3, eventItem.getProductName());
            ps.setBigDecimal(4, eventItem.getOriginalPrice());
            ps.setBigDecimal(5, eventItem.getDiscountPrice());
            ps.setObject(6, LocalDateTime.now());
            ps.setObject(7, LocalDateTime.now());
        });
    }
}
```

**개선 효과**:

-   JPA saveAll → JdbcTemplate batchUpdate로 변경
-   배치 크기 1000으로 설정하여 메모리 효율성 향상
</details>

#### 3\. 이벤트 발행 비동기 처리 + 트랜잭션 일관성 보장
<details>
 <summary><b>구현 예시</b></summary>
 
 #### 문제 상황

```
@Transactional
public EventResponse createEvent(EventCrateRequest request) {
    // ... 데이터 저장 ...

    // 이벤트 발행 (동기적)
    wsEventProducts.forEach(wsEvent -> {
        eventPublisher.publishEvent(wsEvent);
    });

    return new EventResponse(event);
}
```

**문제점**: 이벤트 발행이 동기적으로 처리되어 지연 발생

#### 해결 방안: @TransactionalEventListener 적용

```
@Component
public class NotificationListener {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void addProductDiscountEvent(WSEventProduct event) {
        try {
            log.info("addProductDiscountEvent 시작 - 단일 이벤트: {}", event.product_id());

            ListenProductEvent listenProductEvent = new ListenProductEvent(event);
            notificationService.notifyProductEventMessage(listenProductEvent);

            log.info("addProductDiscountEvent 종료");
        } catch (Exception e) {
            log.error("addProductDiscountEvent 처리 실패 message : {}", e.getMessage());
        }
    }
}
```

**개선 효과**:

-   트랜잭션 커밋 후 이벤트 리스너 실행으로 데이터 일관성 보장
-   비동기 처리로 컨트롤러 응답 시간 단축
-   트랜잭션 롤백 시 이벤트 리스너 미실행으로 안정성 확보
</details>

#### 4\. JPA 연관관계 매핑 제거
<details>
 <summary><b>구현 예시</b></summary>
 
 #### 개선 전: 일대다 연관관계

```
@Entity
public class Event {
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<EventItem> products;
}

@Entity
public class EventItem {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;
}
```

**문제점**:

-   조인 테이블(events\_products)에 대량 insert 쿼리 발생
-   트랜잭션 종료 시 지연 발생
-   메모리 사용량 증가

#### 개선 후: 연관관계 제거

```
@Entity
public class Event {
    @Transient  // 조회용으로만 사용
    private List<EventItem> products;
}

@Entity
public class EventItem {
    private Long eventId;  // 단순 외래키만 저장
}
```

**개선 효과**:

-   조인 테이블 insert 쿼리 제거
-   트랜잭션 종료 시 오버헤드 대폭 감소
-   메모리 사용량 최적화
</details>


### 성능 측정 결과 (개선 후)

```
1단계 - Event 저장 완료: 29ms
2단계 - ProductApiClient 호출 완료: 260ms (조회된 상품 수: 10000)
3단계 - EventItem 객체 생성 완료: 3ms
4단계 - EventItem 벌크 insert 완료: 520ms
5단계 - Event에 EventItem 리스트 설정 완료: 0ms
6단계 - WSEventProduct 객체 생성 완료: 1ms
7단계 - 이벤트 발행 완료: 10ms
=== createEvent 총 실행시간: 823ms ===
8단계 - 컨트롤러 실행: 844ms
```

### 관련 블로그

차준호
- https://juno0112.tistory.com/116 : 도메인주도 설계 및 데이터 흐름 시각화하기
- https://juno0112.tistory.com/117 : 도메인 간 의존성 낮추기 : API 호출 및 스프링 이벤트 구독을 위한 Common 패키지 설계
- https://juno0112.tistory.com/118 : Spring Boot에서 RestTemplate 내부 API 호출 시 JWT 토큰 전달하기



김신영
- https://velog.io/@eggtart21/동시성-제어-구현-i193xytg
- https://velog.io/@eggtart21/동시성-제어-구현
- https://velog.io/@eggtart21/락-시간-정한-이유-공식


이준영
- https://t-era.tistory.com/292 : 현재 웹소켓 기능의 문제점
- https://t-era.tistory.com/293 : 대용량 데이터 삽입 시의 성능 개선

최영재
- https://velog.io/@teopteop/TIL-Spring-%EB%B3%B4%EC%83%81-%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98 : 이벤트 기반 아키텍처에서의 무결성 보완 - 보상 트랜잭션
