package com.example.hotdeal.domain.user.subscribe.domain;

import java.util.List;

import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;

@Getter
public class SubscribeRequest {

	private List<Long> productIds;

	@AssertTrue
	private boolean isValidList() {
		return !productIds.isEmpty();
	}

}