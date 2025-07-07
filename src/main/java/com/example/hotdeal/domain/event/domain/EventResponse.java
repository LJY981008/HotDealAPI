package com.example.hotdeal.domain.event.domain;

import com.example.hotdeal.domain.event.enums.EventType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
public class EventResponse {

    private final Long eventId;
    private final EventType eventType;
    private final int eventDiscount;
    private final LocalDateTime startEventTime;
    private final LocalDateTime endEventTime;
    private final Set<Long> productIds;

    public EventResponse(Event event) {
        this.eventId = event.getEventId();
        this.eventType = event.getEventType();
        this.eventDiscount = event.getEventDiscount();
        this.startEventTime = event.getStartEventTime();
        this.endEventTime = event.getStartEventTime().plusDays(event.getEventDiscount());
        this.productIds = event.getProductIds();
    }
}
