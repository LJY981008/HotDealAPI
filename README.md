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


