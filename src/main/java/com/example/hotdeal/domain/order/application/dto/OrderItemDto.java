package com.example.hotdeal.domain.order.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemDto {
    private final Long productId;
    private final String productName;
    private final Integer quantity;
    private final BigDecimal ProductPrice;

    public OrderItemDto(Long productId, String productName, Integer quantity, BigDecimal productPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        ProductPrice = productPrice;
    }
}
