package com.example.hotdeal.domain.stock;


import com.example.hotdeal.domain.stock.application.StockService;
import com.example.hotdeal.domain.stock.domain.Stock;
import com.example.hotdeal.domain.stock.infra.StockRepository;
import com.example.hotdeal.global.infrastructure.cache.RedisConfig;
import com.example.hotdeal.global.lock.LockRedisRepository;
import com.example.hotdeal.global.lock.LockService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.assertThat;



@SpringBootTest
@Testcontainers
@Import({RedisConfig.class, LockRedisRepository.class, LockService.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class StockConcurrencySuccessTest {


    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    StockRepository repo;
    @Autowired
    StockService service;
    Long stockId;

    @BeforeEach
    void setUp() {
        repo.deleteAll();
        stockId = repo.save(new Stock(1_000)).getId(); // 재고 1000개
    }

    @Test
    void should_success_when_1000_threads_decrease_with_lock() throws InterruptedException {

        int threadCnt = 1_000;
        ExecutorService pool = Executors.newFixedThreadPool(128);
        CountDownLatch latch = new CountDownLatch(threadCnt);

        for (int i = 0; i < threadCnt; i++) {
            pool.submit(() -> {
                try {
                    service.decreaseWithLock(stockId, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        pool.shutdown();

        int remain = repo.findById(stockId).orElseThrow().getQuantity();
        System.out.println("남은 재고 = " + remain);
        assertThat(remain).isZero(); //0개
    }
}
