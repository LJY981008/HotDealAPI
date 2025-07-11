package com.example.hotdeal.domain.common.client.stock.dto;

import com.example.hotdeal.domain.stock.domain.Stock;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class StockResponse {
    private final Long stockId;
    private final Long productId;
    private final int quantity;
    private final boolean available;
    private final LocalDateTime lastUpdated;

    public StockResponse(Stock stock) {
        this.stockId = stock.getId();
        this.productId = stock.getProductId();
        this.quantity = stock.getQuantity();
        this.available = stock.getQuantity() > 0;
        this.lastUpdated = stock.getModifiedAt() != null ? stock.getModifiedAt() : stock.getCreatedAt();
    }
}