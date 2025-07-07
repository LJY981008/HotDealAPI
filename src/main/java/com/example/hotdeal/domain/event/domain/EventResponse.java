package com.example.hotdeal.domain.event.domain;

import com.example.hotdeal.domain.event.enums.EventType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class EventResponse {

    private final Long event_id;
    private final EventType event_type;
    private final int event_discount;
    private final LocalDateTime start_event_time;
    private final LocalDateTime end_event_time;
    private final List<Long> product_ids;

    public EventResponse(Event event) {
        this.event_id = event.getEvent_id();
        this.event_type = event.getEvent_type();
        this.event_discount = event.getEvent_discount();
        this.start_event_time = event.getStart_event_time();
        this.end_event_time = event.getStart_event_time().plusDays(event.getEvent_discount());
        this.product_ids = event.getProduct_ids();
    }
}
