package com.example.hotdeal.domain.product.product.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product_query_view", indexes = {
        @Index(name = "idx_category", columnList = "product_category"),
        @Index(name = "idx_popularity", columnList = "total_order_count, average_rating"),
        @Index(name = "idx_created_at", columnList = "created_at"),
        @Index(name = "idx_discount", columnList = "is_on_discount")
})
public class ProductQueryView {

    @Id
    @Column(name = "product_id")
    private Long productId;

    // 기본 상품 정보
    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "product_description", columnDefinition = "TEXT")
    private String productDescription;

    @Column(name = "product_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal productPrice;

    @Column(name = "product_image_url", length = 500)
    private String productImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_category", length = 50)
    private ProductCategory productCategory;

    // 통계 정보 (비정규화)
    @Column(name = "total_order_count")
    private int totalOrderCount;

    @Column(name = "total_review_count")
    private int totalReviewCount;

    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating;

    // 할인 정보 (비정규화)
    @Column(name = "is_on_discount")
    private boolean isOnDiscount = false;

    @Column(name = "discount_rate")
    private Integer discountRate;

    @Column(name = "discount_price", precision = 10, scale = 2)
    private BigDecimal discountPrice;

    // 메타 정보
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 생성자
    public ProductQueryView(Long productId, String productName, String productDescription,
                            BigDecimal productPrice, String productImageUrl, ProductCategory productCategory,
                            int totalOrderCount, int totalReviewCount, BigDecimal averageRating,
                            boolean isOnDiscount, Integer discountRate, BigDecimal discountPrice,
                            LocalDateTime createdAt) {
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
        this.productImageUrl = productImageUrl;
        this.productCategory = productCategory;
        this.totalOrderCount = totalOrderCount;
        this.totalReviewCount = totalReviewCount;
        this.averageRating = averageRating;
        this.isOnDiscount = isOnDiscount;
        this.discountRate = discountRate;
        this.discountPrice = discountPrice;
        this.createdAt = createdAt;
        this.updatedAt = LocalDateTime.now();
    }
}
