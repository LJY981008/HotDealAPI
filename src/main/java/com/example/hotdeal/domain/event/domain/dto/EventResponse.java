package com.example.hotdeal.domain.event.domain.dto;

import com.example.hotdeal.domain.event.domain.entity.Event;
import com.example.hotdeal.domain.event.domain.entity.EventItem;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class EventResponse {

    private final Long eventId;
    private final List<Long> productId;
    private final LocalDateTime startEventTime;
    private final LocalDateTime endEventTime;

    public EventResponse(Event event) {
        this.eventId = event.getEventId();
        // 연관관계 제거로 인해 products가 null일 수 있으므로 안전하게 처리
        this.productId = event.getProducts() != null ? 
            event.getProducts().stream().map(EventItem::getProductId).collect(Collectors.toList()) : 
            List.of();
        this.startEventTime = event.getStartEventTime();
        this.endEventTime = event.getStartEventTime().plusDays(event.getEventDuration());
    }
}
