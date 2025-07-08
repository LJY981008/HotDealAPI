package com.example.hotdeal.domain.event.domain.dto;

import com.example.hotdeal.domain.event.domain.entity.Event;
import com.example.hotdeal.domain.event.enums.EventType;
import com.example.hotdeal.domain.product.product.domain.AddEventResponse;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class EventResponse {

    private final Long eventId;
    private final EventType eventType;
    private final int eventDiscount;
    private final LocalDateTime startEventTime;
    private final LocalDateTime endEventTime;
    private List<String> productNames = new ArrayList<>();

    public EventResponse(Event event) {
        this.eventId = event.getEventId();
        this.eventType = event.getEventType();
        this.eventDiscount = event.getEventDiscount();
        this.startEventTime = event.getStartEventTime();
        this.endEventTime = event.getStartEventTime().plusDays(event.getEventDuration());
    }
    public EventResponse(Event event, List<AddEventResponse> addEventResponses) {
        this.eventId = event.getEventId();
        this.eventType = event.getEventType();
        this.eventDiscount = event.getEventDiscount();
        this.startEventTime = event.getStartEventTime();
        this.endEventTime = event.getStartEventTime().plusDays(event.getEventDuration());
        this.productNames = addEventResponses != null ? 
            addEventResponses.stream().map(AddEventResponse::getProductName).collect(Collectors.toList()) : 
            new ArrayList<>();
    }
}
