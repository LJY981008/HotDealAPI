package com.example.hotdeal.domain.event.domain.dto;

import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;

import java.util.List;

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
