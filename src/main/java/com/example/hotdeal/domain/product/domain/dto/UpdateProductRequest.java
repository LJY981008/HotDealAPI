package com.example.hotdeal.domain.product.domain.dto;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.URL;

import com.example.hotdeal.domain.product.domain.ProductCategory;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateProductRequest(
	@Size(min = 1, max = 200, message = "상품명은 1~200자 사이여야 합니다")
	String productName,

	@Size(min = 1, max = 1000, message = "상품 설명은 1~1000자 사이여야 합니다")
	String productDescription,

	@DecimalMin(value = "0.0", inclusive = false, message = "상품 가격은 0보다 커야 합니다")
	BigDecimal productPrice,

	@URL(message = "올바른 URL 형식이어야 합니다")
	String productImageUrl,

	@NotNull(message = "상품 카테고리는 필수입니다")
	ProductCategory productCategory
) {
}