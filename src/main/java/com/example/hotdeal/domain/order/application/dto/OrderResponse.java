package com.example.hotdeal.domain.order.application.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class OrderResponse {
    private final Long orderId;
    private final Long userId;
    private final List<OrderItemDto> products;
    private final BigDecimal totalPrice;

    public OrderResponse(Long orderId, Long userId, List<OrderItemDto> products, BigDecimal totalPrice) {
        this.orderId = orderId;
        this.userId = userId;
        this.products = products;
        this.totalPrice = totalPrice;
    }
}
