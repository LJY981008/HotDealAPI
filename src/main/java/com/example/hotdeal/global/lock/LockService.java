package com.example.hotdeal.global.lock;

import java.time.Duration;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockService {

	private final LockRedisRepository lockRedisRepository;

	// 기본 락 타임아웃: 100 ms
	private static final Duration DEFAULT_LOCK_TIMEOUT = Duration.ofMillis(100);
	// 락 획득 재시도 간격: 50ms
	private static final long RETRY_INTERVAL_MS = 50;

	// 최대 대기 시간
	private static final Duration MAX_RETRY_DURATION = Duration.ofSeconds(4);

	//최대 시도 횟수
	private static final int MAX_RETRY_COUNT = 80;   // 50ms × 80 = 4s

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
		long deadline = System.currentTimeMillis() + MAX_RETRY_DURATION.toMillis();
		int attempts = 0;

		while (attempts < MAX_RETRY_COUNT && System.currentTimeMillis() < deadline) {
			attempts++;

			if (Boolean.TRUE.equals(lockRedisRepository.tryLock(key, value, lockTimeout))) {
				log.debug("Lock acquired after {} attempts in {} ms",
					attempts,
					System.currentTimeMillis() - (deadline - MAX_RETRY_DURATION.toMillis()));
				return true;
			}

			try {
				Thread.sleep(RETRY_INTERVAL_MS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return false;
			}
		}
		log.warn("Give-up: attempts={}, elapsed={} ms",
			attempts, MAX_RETRY_DURATION.toMillis() - (deadline - System.currentTimeMillis()));
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