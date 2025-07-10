package com.example.hotdeal.domain.user.subscribe.domain;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

@Getter
public class SubscribeRequest {

    @NotEmpty
    private Long userId;

    private List<Long> productIds;

    @AssertTrue
    private boolean isValidList() {
        return !productIds.isEmpty();
    }
}
