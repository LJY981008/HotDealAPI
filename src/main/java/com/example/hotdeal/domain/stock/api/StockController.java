package com.example.hotdeal.domain.stock.api;

import com.example.hotdeal.domain.stock.application.StockService;
import com.example.hotdeal.domain.common.client.stock.dto.StockResponse;
import com.example.hotdeal.domain.stock.domain.dto.StockSearchRequest;
import com.example.hotdeal.domain.stock.infra.StockRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService service;
    private final StockRepository repository;

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

    //재고 차감
    @PostMapping("/{id}/decrease")
    public void decrease(@PathVariable Long id,
                         @RequestParam int quantity) {
        service.decrease(id, quantity);
    }

    //재고 증가 (관리자)
    @PostMapping("/{id}/increase")
    public void increase(@PathVariable Long id,
                         @RequestParam int quantity) {
        service.increase(id, quantity);
    }

    //재고 리셋 (관리자)
    @PostMapping("/{id}/reset")
    public void reset(@PathVariable Long id,
                      @RequestParam int quantity) {
        service.reset(id, quantity);
    }

    // 남은 수량 조회
    @GetMapping("/{id}/available")
    public int available(@PathVariable Long id) {
        return service.available(id);
    }
}
