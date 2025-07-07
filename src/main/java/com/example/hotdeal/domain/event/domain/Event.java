package com.example.hotdeal.domain.event.domain;

import com.example.hotdeal.domain.event.enums.EventType;
import com.example.hotdeal.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Table(name = "events")
public class Event extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;

    @ElementCollection
    @Column(name = "product_ids")
    private Set<Long> productIds = new HashSet<>();

    @Column(name = "event_type")
    private EventType eventType;

    @Column(name = "event_discount")
    private int eventDiscount;

    @Column(name = "event_duration")
    private int eventDuration;

    @Column(name = "start_event_time")
    private LocalDateTime startEventTime;

    public Event() {}

    public Event(EventType eventType, int eventDiscount, int eventDuration, LocalDateTime startEventTime) {
        this.eventType = eventType;
        this.eventDiscount = eventDiscount;
        this.eventDuration = eventDuration;
        this.startEventTime = startEventTime;
    }

    public void addEventToProduct(List<Long> productIds) {
        this.productIds.addAll(productIds);
    }

    public void removeEventFromProduct(Long productId) {
        this.productIds.remove(productId);
    }
}
