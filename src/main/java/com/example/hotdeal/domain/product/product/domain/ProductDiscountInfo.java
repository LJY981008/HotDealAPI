package com.example.hotdeal.domain.product.product.domain;


import com.example.hotdeal.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product_discount_info")
public class ProductDiscountInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_event_id")
    private Long productEventId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    // 이벤트 관련 가격 정보 (서비스에서 계산해서 저장)
    @Column(name = "original_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal originalPrice; // 원래 가격

    @Column(name = "discount_rate")
    private int discountRate; // 할인율 (퍼센트)

    @Column(name = "discount_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal discountPrice; // 할인된 가격

    // 생성자
    public ProductDiscountInfo(Long productId, BigDecimal originalPrice, int discountRate, BigDecimal discountPrice) {
        this.productId = productId;
        this.originalPrice = originalPrice;
        this.discountRate = discountRate;
        this.discountPrice = discountPrice;
    }
}