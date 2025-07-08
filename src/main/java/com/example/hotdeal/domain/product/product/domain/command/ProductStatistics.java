package com.example.hotdeal.domain.product.product.domain.command;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductStatistics {

    @Column(name = "total_order_count")
    private int totalOrderCount = 0;

    @Column(name = "total_review_count")
    private int totalReviewCount = 0;

    @Column(name = "total_rating_sum")
    private int totalRatingSum = 0;

    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

    public ProductStatistics(int totalOrderCount, int totalReviewCount, int totalRatingSum) {
        this.totalOrderCount = Math.max(0, totalOrderCount);
        this.totalReviewCount = Math.max(0, totalReviewCount);
        this.totalRatingSum = Math.max(0, totalRatingSum);
        this.averageRating = calculateAverageRating();
    }

    private BigDecimal calculateAverageRating() {
        if (totalReviewCount == 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(totalRatingSum)
                .divide(BigDecimal.valueOf(totalReviewCount), 2, RoundingMode.HALF_UP);
    }
}
