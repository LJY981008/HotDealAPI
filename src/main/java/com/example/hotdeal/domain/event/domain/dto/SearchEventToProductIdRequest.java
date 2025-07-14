package com.example.hotdeal.domain.event.domain.dto;

import java.util.List;

import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;

@Getter
public class SearchEventToProductIdRequest {

	private List<Long> productIds;

	@AssertTrue
	public boolean isValidList() {
		return !productIds.isEmpty();
	}

	public SearchEventToProductIdRequest(List<Long> productIds) {
		this.productIds = productIds;
	}

}