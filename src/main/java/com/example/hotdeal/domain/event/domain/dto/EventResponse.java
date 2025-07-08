package com.example.hotdeal.domain.event.domain.dto;

import com.example.hotdeal.domain.event.domain.entity.Event;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class EventResponse {

    private final Long eventId;
    private final LocalDateTime startEventTime;
    private final LocalDateTime endEventTime;

    public EventResponse(Event event) {
        this.eventId = event.getEventId();
        this.startEventTime = event.getStartEventTime();
        this.endEventTime = event.getStartEventTime().plusDays(event.getEventDuration());
    }
}
