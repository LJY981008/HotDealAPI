package com.example.hotdeal.domain.common.springEvent.order;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;

/**
 * 주문 생성 이벤트
 */
@Getter
public class OrderCreatedEvent {

	private final Long orderId;
	private final Long userId;
	private final List<OrderItemInfo> orderItems;
	private final LocalDateTime orderTime;

	public OrderCreatedEvent(Long orderId, Long userId, List<OrderItemInfo> orderItems) {
		this.orderId = orderId;
		this.userId = userId;
		this.orderItems = orderItems;
		this.orderTime = LocalDateTime.now();
	}

	@Getter
	public static class OrderItemInfo {
		private final Long productId;
		private final int quantity;
		private final String productName;

		public OrderItemInfo(Long productId, int quantity, String productName) {
			this.productId = productId;
			this.quantity = quantity;
			this.productName = productName;
		}
	}

}