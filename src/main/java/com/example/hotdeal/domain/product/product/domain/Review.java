package com.example.hotdeal.domain.product.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Column(name = "review_content", length = 1000)
    private String content;

    @Column(name = "review_rating")
    private int rating;

    @Column(name = "reviewer_name", length = 50)
    private String reviewerName;

    @Column(name = "review_created_at")
    private LocalDateTime createdAt;

    @Column(name = "review_user_id")
    private Long userId;

    public Review(String content, int rating, String reviewerName, Long userId) {
        this.content = content;
        this.rating = rating;
        this.reviewerName = reviewerName;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
    }
}
