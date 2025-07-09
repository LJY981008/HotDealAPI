package com.example.hotdeal.domain.stock.api;

import com.example.hotdeal.domain.stock.application.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService service;

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
