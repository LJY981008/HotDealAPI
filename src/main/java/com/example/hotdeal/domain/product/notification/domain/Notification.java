package com.example.hotdeal.domain.product.notification.domain;

import com.example.hotdeal.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "notification")
public class Notification extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notification_id;

    private Long product_id;
    private Long event_id;
    private String notification_message;
}
