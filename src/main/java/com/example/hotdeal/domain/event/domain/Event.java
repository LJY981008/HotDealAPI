package com.example.hotdeal.domain.event.domain;

import com.example.hotdeal.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "events")
public class Event extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long event_id;

    private Long product_id;
    private String event_type;
    private int event_discount;
    private int event_duration;
    private LocalDateTime start_event_time;
}
