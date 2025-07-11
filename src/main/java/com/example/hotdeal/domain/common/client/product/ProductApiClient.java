package com.example.hotdeal.domain.common.client.product;

import com.example.hotdeal.domain.common.client.product.dto.SearchProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductApiClient {

    private final RestTemplate restTemplate;
    private static final String PRODUCT_API_BASE_URL = "http://localhost:8080/api/products";

    /**
     * 상품 단건 조회
     */
    public SearchProductResponse getProduct(Long productId) {
        log.debug("상품 단건 조회: productId={}", productId);

        String url = PRODUCT_API_BASE_URL + "/" + productId;
        return restTemplate.getForObject(url, SearchProductResponse.class);
    }
}