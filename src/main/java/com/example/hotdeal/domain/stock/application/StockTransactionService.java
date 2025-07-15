package com.example.hotdeal.domain.stock.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hotdeal.domain.stock.domain.Stock;
import com.example.hotdeal.domain.stock.infra.StockRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class StockTransactionService {

	private final StockRepository repository;

	@Transactional
	public void decreaseInNewTransaction(Long stockId, int quantity) {
		Stock stock = repository.findById(stockId)
			.orElseThrow(() -> new IllegalArgumentException("재고를 찾을 수 없습니다. ID = " + stockId));

		if (stock.getQuantity() < quantity) {
			throw new IllegalArgumentException("재고가 부족합니다. 현재 재고: " + stock.getQuantity() + ", Requested: " + quantity);
		}

		stock.decrease(quantity);
		log.info("재고 차감 완료. ID: {}, 차감 수량: {}, 남은 재고: {}",
			stockId, quantity, stock.getQuantity());
	}

}