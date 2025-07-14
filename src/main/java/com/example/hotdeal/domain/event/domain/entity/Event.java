package com.example.hotdeal.domain.event.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.hotdeal.domain.event.enums.EventType;
import com.example.hotdeal.global.model.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Table(name = "events")
public class Event extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_id")
	private Long eventId;

	@Setter
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<EventItem> products;

	@Column(name = "event_type")
	private EventType eventType;

	@Column(name = "event_discount")
	private BigDecimal eventDiscount;

	@Column(name = "event_duration")
	private int eventDuration;

	@Column(name = "start_event_time")
	private LocalDateTime startEventTime;

	@Column(name = "end_event_time")
	private LocalDateTime endEventTime;

	public Event() {
	}

	public Event(EventType eventType, BigDecimal eventDiscount, int eventDuration, LocalDateTime startEventTime) {
		this.eventType = eventType;
		this.eventDiscount = eventDiscount;
		this.eventDuration = eventDuration;
		this.startEventTime = startEventTime;
		this.endEventTime = startEventTime.plusDays(eventDuration);
	}

}