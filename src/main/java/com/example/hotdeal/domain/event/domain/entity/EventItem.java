package com.example.hotdeal.domain.event.domain.entity;

import com.example.hotdeal.domain.common.client.product.dto.SearchProductResponse;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Getter
@Table(name = "event_items") // 자식 테이블
public class EventItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;
    @Column(name = "product_id")
    private Long productId;
    @Column(name = "discount_price")
    private BigDecimal discountPrice;
    @Column(name = "original_price")
    private BigDecimal originalPrice;
    @Column(name = "product_name")
    private String productName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    public EventItem(SearchProductResponse response, BigDecimal discount, Event event) {
        this.productId = response.getProductId();
        this.productName = response.getProductName();
        this.originalPrice = response.getOriginalPrice();
        BigDecimal finalRate = BigDecimal.ONE.subtract(discount.divide(new BigDecimal("100")));
        this.discountPrice = response.getOriginalPrice().multiply(finalRate);
        this.event = event;
    }

    public EventItem() {

    }
}
