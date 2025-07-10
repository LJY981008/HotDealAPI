package com.example.hotdeal.domain.common.client.product;

import com.example.hotdeal.domain.product.product.domain.dto.SearchProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

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

    /**
     * 상품 다건 조회
     */
    public List<SearchProductResponse> getProducts(List<Long> productIds) {
        log.debug("상품 다건 조회: productIds={}", productIds);

        ProductSearchRequest request = new ProductSearchRequest(productIds);
        URI uri = UriComponentsBuilder
                .fromUriString(PRODUCT_API_BASE_URL)
                .path("/search-product")
                .encode()
                .build()
                .toUri();

        ResponseEntity<List<SearchProductResponse>> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<List<SearchProductResponse>>() {}
        );

        return response.getBody();
    }

    // 내부 요청 DTO
    private static class ProductSearchRequest {
        private final List<Long> productIds;

        public ProductSearchRequest(List<Long> productIds) {
            this.productIds = productIds;
        }

        public List<Long> getProductIds() {
            return productIds;
        }
    }
}