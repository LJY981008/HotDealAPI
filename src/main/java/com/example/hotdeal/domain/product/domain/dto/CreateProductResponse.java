package com.example.hotdeal.domain.product.domain.dto;

import java.math.BigDecimal;

import com.example.hotdeal.domain.product.domain.Product;

import lombok.Getter;

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