package com.example.hotdeal.domain.event.domain.dto;

import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;

import java.util.List;

/**
 * 레스트 템플릿을 통해 값을 넘기기 위한 DTO
 */
@Getter
public class EventAddProductRequest {

    private List<Long> productIds;

    @AssertTrue
    private boolean isValidList() {
        return !productIds.isEmpty();
    }

    public EventAddProductRequest(List<Long> productIds) {
        this.productIds = productIds;
    }
}
