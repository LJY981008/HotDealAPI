package com.example.hotdeal.domain.event.domain.dto;

import com.example.hotdeal.domain.event.enums.EventType;

import java.math.BigDecimal;

public record WSEventProduct (
        Long event_id,
        Long product_id,
        EventType eventType,
        String productName,
        BigDecimal originalPrice,
        BigDecimal discountPrice,
        BigDecimal discount
) {
}
