package com.example.hotdeal.domain.event.domain.entity;

import com.example.hotdeal.domain.common.client.product.dto.SearchProductResponse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "event_items")
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

    @Column(name = "event_id")
    private Long eventId;

    public EventItem(SearchProductResponse response, BigDecimal discount, Long eventId) {
        this.productId = response.getProductId();
        this.productName = response.getProductName();
        this.originalPrice = response.getOriginalPrice();
        BigDecimal finalRate = BigDecimal.ONE.subtract(discount.divide(new BigDecimal("100")));
        this.discountPrice = response.getOriginalPrice().multiply(finalRate);
        this.eventId = eventId;  // Event 엔티티 대신 ID만 저장
    }

    public EventItem() {

    }
}
