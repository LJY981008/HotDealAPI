package com.example.hotdeal.domain.user.subscribe.domain;

import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;

import java.util.List;

@Getter
public class SubscribeRequest {

    private List<Long> productIds;

    @AssertTrue
    private boolean isValidList() {
        return !productIds.isEmpty();
    }
}
