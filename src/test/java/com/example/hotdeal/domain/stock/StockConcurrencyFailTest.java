package com.example.hotdeal.domain.stock;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.hotdeal.domain.stock.application.StockService;
import com.example.hotdeal.domain.stock.domain.Stock;
import com.example.hotdeal.domain.stock.infra.StockRepository;

@DataJpaTest
@Import(StockService.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class StockConcurrencyFailTest {

	@Autowired
	StockRepository repo;
	@Autowired
	StockService service;
	Long stockId;

	@BeforeEach
	void setUp() {
		stockId = repo.save(new Stock(1_000)).getId(); // 재고 1000개
	}

	@Test
	void should_fail_when_1000_threads_decrease() throws InterruptedException {

		int threadCnt = 1_000;
		ExecutorService pool = Executors.newFixedThreadPool(128);
		CountDownLatch latch = new CountDownLatch(threadCnt);

		for (int i = 0; i < threadCnt; i++) {
			pool.submit(() -> {
				try {
					service.decrease(stockId, 1);
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
		assertThat(remain).isZero(); //실패
	}

}