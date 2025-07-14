package com.example.hotdeal.domain.common.client.product.dto;

import java.math.BigDecimal;

import com.example.hotdeal.domain.product.domain.Product;
import com.example.hotdeal.domain.product.domain.ProductCategory;

import lombok.Getter;

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

	public SearchProductResponse() {
	}

	public SearchProductResponse(Product product) {
		this.productId = product.getProductId();
		this.productName = product.getProductName();
		this.originalPrice = product.getProductPrice();
		this.productDescription = product.getProductDescription();
		this.productCategory = product.getProductCategory();
		this.productImageUri = product.getProductImageUrl();
	}

}