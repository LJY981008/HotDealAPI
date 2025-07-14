package com.example.hotdeal.domain.stock.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.hotdeal.domain.common.client.stock.dto.StockResponse;
import com.example.hotdeal.domain.stock.application.StockService;
import com.example.hotdeal.domain.stock.domain.dto.StockSearchRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

	private final StockService service;

	// 다건 상품 재고 조회
	@PostMapping("/search")
	public ResponseEntity<List<StockResponse>> searchStocks(
		@Valid @RequestBody StockSearchRequest request
	) {
		List<StockResponse> stocks = service.searchStocksByProductIds(request.getProductIds());
		return ResponseEntity.ok(stocks);
	}

	// 단일 상품 재고 조회
	@GetMapping("/product/{productId}")
	public ResponseEntity<StockResponse> getStockByProductId(@PathVariable Long productId) {
		StockResponse stock = service.getStockByProductId(productId);
		return ResponseEntity.ok(stock);
	}

	// 재고 증가 (관리자용) - 상품 검증 포함
	@PostMapping("/product/{productId}/increase")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<StockResponse> increaseStock(
		@PathVariable Long productId,
		@RequestParam int quantity
	) {
		StockResponse response = service.increaseStock(productId, quantity);
		return ResponseEntity.ok(response);
	}

	// 재고 초기화 (관리자용) - 상품 검증 포함
	@PostMapping("/product/{productId}/reset")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<StockResponse> resetStock(
		@PathVariable Long productId,
		@RequestParam int quantity
	) {
		StockResponse response = service.resetStock(productId, quantity);
		return ResponseEntity.ok(response);
	}

}