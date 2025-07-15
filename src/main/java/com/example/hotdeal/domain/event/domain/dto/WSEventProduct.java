package com.example.hotdeal.domain.event.domain.dto;

import java.math.BigDecimal;

import com.example.hotdeal.domain.event.enums.EventType;

public record WSEventProduct(
	Long event_id,
	Long product_id,
	EventType eventType,
	String productName,
	BigDecimal originalPrice,
	BigDecimal discountPrice,
	BigDecimal discount
) {
}