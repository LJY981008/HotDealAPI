package com.example.hotdeal.domain.stock.domain;

import com.example.hotdeal.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "stock")
public class Stock extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stock_id;

    private Long product_id;
    private int stock_count;
}
