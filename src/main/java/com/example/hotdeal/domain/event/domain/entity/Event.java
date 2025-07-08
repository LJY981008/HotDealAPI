package com.example.hotdeal.domain.event.domain.entity;

import com.example.hotdeal.domain.event.enums.EventType;
import com.example.hotdeal.domain.product.product.domain.command.Product;
import com.example.hotdeal.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Table(name = "events")
public class Event extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Product> products;

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
}
