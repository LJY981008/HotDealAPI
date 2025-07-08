package com.example.hotdeal.domain.product.product.domain.dto;

import com.example.hotdeal.domain.product.product.domain.Product;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CreateProductResponse {

    private final Long productId;
    private final String productName;
    private final String productDescription;
    private final BigDecimal productPrice;

    public CreateProductResponse(Product product) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.productDescription = product.getProductDescription();
        this.productPrice = product.getProductPrice();
    }
}
