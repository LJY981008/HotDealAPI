package com.example.hotdeal.domain.event.domain;

import com.example.hotdeal.global.event.model.DomainEvent;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ProductEventAddedEvent implements DomainEvent {

    private final Long eventId;
    private final List<Long> productIds;
    private final LocalDateTime occurredAt;

    public ProductEventAddedEvent(Long eventId, List<Long> productIds) {
        this.eventId = eventId;
        this.productIds = productIds;
        this.occurredAt = LocalDateTime.now();
    }

    @Override
    public String getEventType() {
        return "ProductEventAdded";
    }
}
