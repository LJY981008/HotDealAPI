package com.example.hotdeal.domain.user.subscribe.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "product_subscribe")
public class Subscribe {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscribe_id")
    private Long subscribeId;

    @Column(name = "user_id")
    private Long userId;
    @Column(name = "product_id")
    private Long productId;

    public Subscribe(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
    }
}
