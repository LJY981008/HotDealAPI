package com.example.hotdeal.domain.event.domain;

import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;

import java.util.List;

@Getter
public class EventAddProductRequest {

    private List<Long> productIds;

    @AssertTrue
    private boolean verifyList() {
        return !productIds.isEmpty();
    }
}
