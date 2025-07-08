package com.example.hotdeal.domain.product.product.domain;

import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;

import java.util.List;

@Getter
public class AddEventRequest {

    List<Long> productIds;

    @AssertTrue
    private boolean isValidList() {
        return !productIds.isEmpty();
    }
}
