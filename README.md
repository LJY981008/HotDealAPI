#  HotDeal - 실시간 핫딜 이벤트 시스템

##  목차
- [프로젝트 소개](#프로젝트-소개)
- [주요 기능](#주요-기능)
- [기술 스택](#기술-스택)
- [시스템 아키텍처](#시스템-아키텍처)
- [프로젝트 구조](#프로젝트-구조)
- [ERD](#erd)
- [API 명세](#api-명세)
- [실행 방법](#실행-방법)
- [환경 설정](#환경-설정)
- [주요 비즈니스 로직](#주요-비즈니스-로직)

## 프로젝트 소개
HotDeal은 실시간 할인 이벤트 관리 및 주문 처리를 위한 시스템입니다. 
- 동시성 제어를 통한 안정적인 재고 관리
- WebSocket을 활용한 실시간 이벤트 알림
- 도메인 간 분리와 향후 확장성을 고려해 내부 API 통신 방식 적용

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
- Redis (캐싱 및 분산 락)


### 기타
- Redisson
- JWT
- Lombok
- TestContainers

## 시스템 아키텍처



## 프로젝트 구조
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

## ERD

## API 명세



## 실행 방법

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

## 환경 설정

### .env 파일 (환경 변수)
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

### application.yml
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

### application-test.yml
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

### docker-compose.yml (선택사항)
Docker를 사용하여 인프라를 구성하는 경우:
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

### 테스트 환경
- 테스트 실행 시 TestContainers가 자동으로 Redis 컨테이너를 생성
- 통합 테스트에서는 실제 MySQL과 TestContainers Redis를 함께 사용
- 테스트 프로파일(`@ActiveProfiles("test")`)로 별도 설정 적용

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
- (트랜잭션 롤백 및 보상 처리)
- Spring Event를 활용한 실패 시 보상 트랜잭션

# 트러블 슈팅

## 목차
1. [도메인 간 데이터 참조 방식 설계](#1-도메인-간-데이터-참조-방식-설계)
2. [내부 API 호출 시 인증 토큰 전달 문제](#2-내부-api-호출-시-인증-토큰-전달-문제)
3. [HTTP 클라이언트 선택](#3-http-클라이언트-선택-resttemplate-vs-webclient)
4. [Auth와 User 간의 데이터 일관성 문제](#4-auth와-user-간의-데이터-일관성-문제)
5. [웹소켓 알림 시스템 최적화](#5-웹소켓-알림-시스템-최적화)

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


