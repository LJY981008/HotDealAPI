package com.example.hotdeal.domain.event.domain;

import com.example.hotdeal.domain.event.enums.EventType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class EventCrateRequest {

    @NotNull
    private EventType eventType;
    @NotNull
    private int eventDiscount;
    @NotNull
    private int eventDuration;
    @NotNull
    private LocalDateTime startEventTime;

    @AssertTrue
    private boolean varifyDuration(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endtime = startEventTime.plusDays(eventDuration);
        return now.isAfter(endtime);
    }

    public Event toEvent(){
        return new Event(this.eventType, this.eventDiscount, this.eventDuration, this.startEventTime);
    }
}
