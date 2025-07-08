package com.example.hotdeal.domain.product.product.infra;

import com.example.hotdeal.domain.product.product.domain.AddEventResponse;

import java.util.List;

public interface ProductRepositoryCustom {
    List<AddEventResponse> findProductsByIds(List<Long> ids);
    long updateProductEventIds(List<Long> productIds, Long eventId);
}
