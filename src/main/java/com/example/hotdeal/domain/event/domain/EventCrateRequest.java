package com.example.hotdeal.domain.event.domain;

import com.example.hotdeal.domain.event.enums.EventType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class EventCrateRequest {

    @NotNull
    private EventType event_type;
    @NotNull
    private int event_discount;
    @NotNull
    private int event_duration;
    @NotNull
    private LocalDateTime start_event_time;

    @AssertTrue
    private boolean varifyDuration(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endtime = start_event_time.plusDays(event_duration);
        return now.isAfter(endtime);
    }

    public Event toEvent(){
        return new Event(this.event_type, this.event_discount, this.event_duration, this.start_event_time);
    }
}
