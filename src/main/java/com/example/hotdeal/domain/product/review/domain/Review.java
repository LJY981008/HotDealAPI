package com.example.hotdeal.domain.product.review.domain;

import com.example.hotdeal.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reviews")
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "review_content", length = 1000)
    private String content;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "reviewer_name", length = 50)
    private String reviewerName;

    public Review(Long productId, Long userId, String content, int rating, String reviewerName) {
        this.productId = productId;
        this.userId = userId;
        this.content = content;
        this.rating = rating;
        this.reviewerName = reviewerName;
    }
}