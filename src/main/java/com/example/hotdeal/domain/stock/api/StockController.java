package com.example.hotdeal.domain.stock.api;

import com.example.hotdeal.domain.stock.application.StockService;
import com.example.hotdeal.domain.common.client.stock.dto.StockResponse;
import com.example.hotdeal.domain.stock.domain.dto.StockSearchRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // === 기존 메서드들 ===

//    // 재고 차감 (stockId 기반)
//    @PostMapping("/{stockId}/decrease")
//    public ResponseEntity<Void> decrease(
//            @PathVariable Long stockId,
//            @RequestParam int quantity
//    ) {
//        service.decrease(stockId, quantity);
//        return ResponseEntity.ok().build();
//    }
//
//    // 재고 증가 (관리자)
//    @PostMapping("/product/{productId}/increase")
//    @PreAuthorize("hasRole('ADMIN')")
//    public void increase(@PathVariable Long id,
//                         @RequestParam int quantity) {
//        service.increase(id, quantity);
//    }
//
//    // 재고 리셋 (관리자)
//    @PostMapping("/{id}/reset")
//    public void reset(@PathVariable Long id,
//                      @RequestParam int quantity) {
//        service.reset(id, quantity);
//    }
//
//    // 남은 수량 조회
//    @GetMapping("/{id}/available")
//    public int available(@PathVariable Long id) {
//        return service.available(id);
//    }
}
