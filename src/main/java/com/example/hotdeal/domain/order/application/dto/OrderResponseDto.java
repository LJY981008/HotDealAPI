package com.example.hotdeal.domain.order.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.hotdeal.domain.order.enums.OrderStatus;

import lombok.Getter;

@Getter
public class OrderResponseDto {

	private final Long userId;
	private final Long orderId;
	private final OrderItemDto orderItem;
	private final Integer totalProductCount;
	private final BigDecimal totalOrderPrice;
	private final LocalDateTime orderDate;
	private final OrderStatus status;

	public OrderResponseDto(Long userId, Long orderId, OrderItemDto orderItem,
		int totalProductCount, BigDecimal totalOrderPrice,
		LocalDateTime orderDate, OrderStatus status) {
		this.userId = userId;
		this.orderId = orderId;
		this.orderItem = orderItem;
		this.totalProductCount = totalProductCount;
		this.totalOrderPrice = totalOrderPrice;
		this.orderDate = orderDate;
		this.status = status;
	}

}