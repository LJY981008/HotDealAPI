package com.example.hotdeal.domain.common.client.event.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.hotdeal.domain.event.domain.entity.Event;
import com.example.hotdeal.domain.event.domain.entity.EventItem;
import com.example.hotdeal.domain.event.enums.EventType;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EventProductResponse {

	private Long eventId;
	private Long productId;
	private EventType eventType;
	private BigDecimal eventDiscount;
	private BigDecimal discountPrice;
	private int eventDuration;
	private LocalDateTime startEventTime;
	private LocalDateTime endEventTime;

	public EventProductResponse(Event event, EventItem eventItem) {
		this.eventId = event.getEventId();
		this.productId = eventItem.getProductId();
		this.eventType = event.getEventType();
		this.eventDiscount = event.getEventDiscount();
		this.eventDuration = event.getEventDuration();
		this.startEventTime = event.getStartEventTime();
		this.endEventTime = event.getEndEventTime();
		this.discountPrice = eventItem.getDiscountPrice();
	}

}