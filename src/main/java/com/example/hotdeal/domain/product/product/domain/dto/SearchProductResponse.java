package com.example.hotdeal.domain.product.product.domain.dto;

import com.example.hotdeal.domain.product.product.domain.Product;
import com.example.hotdeal.domain.product.product.domain.ProductCategory;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 프로덕트 조회 결과
 */
@Getter
public class SearchProductResponse {
    private Long productId;
    private String productName;
    private BigDecimal originalPrice;
    private String productDescription;
    private String productImageUri;
    private ProductCategory productCategory;

    public SearchProductResponse() {}

    public SearchProductResponse(Product product) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.originalPrice = product.getProductPrice();
        this.productDescription = product.getProductDescription();
        this.productCategory = product.getProductCategory();
        this.productImageUri = product.getProductImageUrl();
    }
}
