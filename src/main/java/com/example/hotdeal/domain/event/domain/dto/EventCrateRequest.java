package com.example.hotdeal.domain.event.domain.dto;

import com.example.hotdeal.domain.event.domain.entity.Event;
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

    @AssertTrue(message = "이벤트 종료 시간은 현재 시간보다 이후여야 합니다")
    private boolean isEndTimeValid() {
        LocalDateTime endTime = startEventTime.plusDays(eventDuration);
        return endTime.isAfter(LocalDateTime.now());
    }

    public Event toEvent(){
        return new Event(this.eventType, this.eventDiscount, this.eventDuration, this.startEventTime);
    }
}
