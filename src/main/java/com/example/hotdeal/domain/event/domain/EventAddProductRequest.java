package com.example.hotdeal.domain.event.domain;

import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;

import java.util.List;

@Getter
public class EventAddProductRequest {

    private List<Long> product_ids;

    @AssertTrue
    private boolean verifyList() {
        return !product_ids.isEmpty();
    }
}
