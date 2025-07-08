package com.example.hotdeal.domain.product.product.domain;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class AddEventResponse {
    private Long productId;
    private String productName;
    private Long productEventId;

    public AddEventResponse() {}

    @QueryProjection
    public AddEventResponse(Product product) {
        this.productId = product.getProductId();
        this.productName = product.getProduct_name();
        this.productEventId = product.getProduct_event_id();
    }
}
