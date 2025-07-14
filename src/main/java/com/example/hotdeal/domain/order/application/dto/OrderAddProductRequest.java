package com.example.hotdeal.domain.order.application.dto;

import java.util.List;

import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderAddProductRequest {

	private List<Long> productIds;

	@AssertTrue
	private boolean isValidList() {
		return !productIds.isEmpty();
	}

	public OrderAddProductRequest(List<Long> productIds) {
		this.productIds = productIds;
	}

}