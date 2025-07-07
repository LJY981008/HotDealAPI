package com.example.hotdeal.domain.order.application.dto;

import com.example.hotdeal.domain.order.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;



@Getter
@AllArgsConstructor
public class OrderResponseDto {
    private final Long orderId;
    private final Long productId;
    private final Long totalOrderPrice;
    private final int totalProductCount;
    private final OrderStatus orderStatus;
}
