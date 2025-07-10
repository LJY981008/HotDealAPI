package com.example.hotdeal.domain.order.application.dto;

import com.example.hotdeal.domain.order.enums.OrderStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderItemResponseDto {
    private final Long userId;
    private final Long orderId;
    private final List<OrderItemDto> items;
    private final int totalOrderCount;
    private final BigDecimal totalOrderPrice;
    private final LocalDateTime orderDate;
    private final OrderStatus status;

    public OrderItemResponseDto(Long userId, Long orderId,
                                List<OrderItemDto> items, int totalOrderCount,
                                BigDecimal totalOrderPrice, LocalDateTime orderDate,
                                OrderStatus status) {
        this.userId = userId;
        this.orderId = orderId;
        this.items = items;
        this.totalOrderCount = totalOrderCount;
        this.totalOrderPrice = totalOrderPrice;
        this.orderDate = orderDate;
        this.status = status;
    }
}
