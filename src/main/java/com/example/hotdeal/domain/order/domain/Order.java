package com.example.hotdeal.domain.order.domain;

import com.example.hotdeal.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Table(name = "orders")
public class Order extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long order_id;

    private Long user_id;
    private int order_total_price;
    private LocalDateTime order_time;
    private List<Long> order_item_ids;
}
