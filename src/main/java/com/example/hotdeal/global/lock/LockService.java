package com.example.hotdeal.global.lock;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockService {


    private final LockRedisRepository lockRedisRepository;


    // 기본 락 타임아웃: 2초
    private static final Duration DEFAULT_LOCK_TIMEOUT = Duration.ofSeconds(2);
    // 락 획득 재시도 간격: 100ms
    private static final long RETRY_INTERVAL_MS = 100;
    // 최대 재시도 시간: 3초
    private static final Duration MAX_RETRY_DURATION = Duration.ofSeconds(3);

    /**
     * 락을 획득하고 작업을 실행
     *
     * @param key  락 키 (예: "stock:1")
     * @param task 실행할 작업
     * @param <T>  반환 타입
     * @return 작업 실행 결과
     */
    public <T> T executeWithLock(String key, Supplier<T> task) {
        return executeWithLock(key, task, DEFAULT_LOCK_TIMEOUT);
    }

    /**
     * 락을 획득하고 작업을 실행 (타임아웃 지정)
     *
     * @param key         락 키
     * @param task        실행할 작업
     * @param lockTimeout 락 유지 시간
     * @param <T>         반환 타입
     * @return 작업 실행 결과
     */
    public <T> T executeWithLock(String key, Supplier<T> task, Duration lockTimeout) {
        String lockValue = generateLockValue();

        if (!acquireLock(key, lockValue, lockTimeout)) {
            throw new LockAcquisitionException("Failed to acquire lock for key: " + key);
        }

        try {
            return task.get();
        } finally {
            releaseLock(key, lockValue);
        }
    }

    /**
     * 락 획득 시도 (재시도 로직 포함)
     */
    private boolean acquireLock(String key, String value, Duration lockTimeout) {
        long startTime = System.currentTimeMillis();
        long maxRetryTime = MAX_RETRY_DURATION.toMillis();

        while (System.currentTimeMillis() - startTime < maxRetryTime) {
            Boolean acquired = lockRedisRepository.tryLock(key, value, lockTimeout);
            if (Boolean.TRUE.equals(acquired)) {
                return true;
            }

            try {
                Thread.sleep(RETRY_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        return false;
    }

    /**
     * 락 해제
     */
    private void releaseLock(String key, String value) {
        try {
            lockRedisRepository.unlock(key, value);
        } catch (Exception e) {
            log.error("Error releasing lock for key: {}", key, e);
        }
    }
        /**
         * 락 소유자 식별값 생성
         */
        private String generateLockValue() {
            return UUID.randomUUID().toString();
        }
    }
