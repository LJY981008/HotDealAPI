package com.example.hotdeal.global.lock;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Collections;

@Repository
@RequiredArgsConstructor
public class LockRedisRepository {


    private final RedisTemplate<String, String> redisTemplate;

    private static final String LOCK_PREFIX = "stock:lock:";

    /**
     * 락 획득 시도
     * @param key 락 키
     * @param value 락 소유자 식별값
     * @param duration 락 유지 시간
     * @return 락 획득 성공 여부
     */
    public Boolean tryLock(String key, String value, Duration duration) {
        String lockKey = generateKey(key);
        return redisTemplate.opsForValue()
                .setIfAbsent(lockKey, value, duration);
    }



    /**
     * 락 해제 (Lua 스크립트 사용 - 락 소유자만 해제 가능)
     * @param key 락 키
     * @param value 락 소유자 식별값
     * @return 락 해제 성공 여부
     */
    public Boolean unlock(String key, String value) {
        String lockKey = generateKey(key);
        String script =
                "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "  return redis.call('del', KEYS[1]) " +
                        "else " +
                        "  return 0 " +
                        "end";

        Long result = redisTemplate.execute(
                new DefaultRedisScript<>(script, Long.class),
                Collections.singletonList(lockKey),
                value
        );

        return result != null && result > 0;
    }



    private String generateKey(String key) {
        return LOCK_PREFIX + key;
    }
}
