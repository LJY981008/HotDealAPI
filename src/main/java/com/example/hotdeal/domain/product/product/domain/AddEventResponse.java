package com.example.hotdeal.domain.product.product.domain;

import lombok.Getter;

@Getter
public class AddEventResponse {

    private Long productId;
    private String productName;
    private Long eventId;

    public AddEventResponse(Product product) {
        this.productId = product.getProductId();
        this.productName = product.getProduct_name();
        this.eventId = product.getProduct_event_id();
    }
}
