package com.example.hotdeal.domain.product.product.domain.command.dto;

import com.example.hotdeal.domain.product.product.domain.command.Product;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProductUpdateResponse {

    private final Long productId;
    private final String productName;
    private final String productDescription;
    private final BigDecimal productPrice;

    public ProductUpdateResponse(Product product) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.productDescription = product.getProductDescription();
        this.productPrice = product.getProductPrice();
    }
}