package com.example.hotdeal.domain.order.domain;

import com.example.hotdeal.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "order_items")
public class OrderItem extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long item_id;

    private Long product_id;

    @Column(name = "order_item_count")
    private int order_item_count;

    @Column(name = "order_item_total_price")
    private int item_total_price;


}
