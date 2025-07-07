package com.example.hotdeal.domain.order.domain;

import com.example.hotdeal.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "order_items")
public class OrderItem extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long item_id;

    private Long order_id;
    private Long product_id;
    private int order_item_count;
    private int item_total_price;
}
