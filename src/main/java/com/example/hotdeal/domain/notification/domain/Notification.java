package com.example.hotdeal.domain.notification.domain;

import com.example.hotdeal.global.model.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "notification")
public class Notification extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long notification_id;

	private Long product_id;
	private Long event_id;
	private String notification_message;

	public Notification(Long product_id, Long event_id, String notification_message) {
		this.product_id = product_id;
		this.event_id = event_id;
		this.notification_message = notification_message;
	}

	public Notification() {
	}

}