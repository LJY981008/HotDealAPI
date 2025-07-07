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
    private Long event_id;

    private Set<Long> product_ids;
    private EventType event_type;
    private int event_discount;
    private int event_duration;
    private LocalDateTime start_event_time;

    public Event() {}

    public Event(EventType event_type, int event_discount, int event_duration, LocalDateTime start_event_time) {
        this.event_type = event_type;
        this.event_discount = event_discount;
        this.event_duration = event_duration;
        this.start_event_time = start_event_time;
        this.product_ids = new HashSet<>();
    }

    public void addEventToProduct(List<Long> product_ids) {
        this.product_ids.addAll(product_ids);
    }

    public void removeEventFromProduct(Long product_id) {
        this.product_ids.remove(product_id);
    }
}
