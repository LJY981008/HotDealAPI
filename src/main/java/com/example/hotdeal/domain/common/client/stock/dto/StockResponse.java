package com.example.hotdeal.domain.common.client.stock.dto;

import com.example.hotdeal.domain.stock.domain.Stock;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class StockResponse {
    private Long stockId;
    private Long productId;
    private int quantity;
    private boolean available;
    private LocalDateTime lastUpdated;

    public StockResponse(Stock stock) {
        this.stockId = stock.getId();
        this.productId = stock.getProductId();
        this.quantity = stock.getQuantity();
        this.available = stock.getQuantity() > 0;
        this.lastUpdated = stock.getModifiedAt() != null ? stock.getModifiedAt() : stock.getCreatedAt();
    }
}