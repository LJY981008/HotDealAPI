package com.example.hotdeal.global.lock;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import java.util.function.Supplier;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedissonLockService {

    private final RedissonClient redisson;

    private static final long WAIT_TIME_MS = 4_000; //lock 획득 시도 대기시간 4초
    private static final long LEASE_TIME_MS = 3_000; // 락 유지 시간 3초

    public <T> T executeWithLock(String key, Supplier<T> task) {
        RLock lock = redisson.getLock("stock:lock:" + key);
        boolean acquired = false;
        try {
            acquired = lock.tryLock(WAIT_TIME_MS, LEASE_TIME_MS, TimeUnit.MILLISECONDS);
            if (!acquired) throw new LockAcquisitionException("Failed to acquire lock for key: " + key);
            return task.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LockAcquisitionException("Interrupted while waiting for lock" + e);

        } finally {
            if (acquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
