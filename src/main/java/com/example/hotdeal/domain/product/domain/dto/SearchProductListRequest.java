package com.example.hotdeal.domain.product.domain.dto;

import java.util.List;

import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;

/**
 * 조회 요청하는 프로덕트의 아이디 리스트 DTO
 */
@Getter
public class SearchProductListRequest {

	List<Long> productIds;

	@AssertTrue
	private boolean isValidList() {
		return !productIds.isEmpty();
	}

	public SearchProductListRequest(List<Long> productIds) {
		this.productIds = productIds;
	}

}