package com.example.hotdeal.domain.common.client.stock;

import com.example.hotdeal.domain.common.client.stock.dto.StockResponse;
import com.example.hotdeal.domain.stock.domain.dto.StockSearchRequest;
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
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockRestClient {

    private final RestTemplate restTemplate;
    private static final String BASE_URL = "http://localhost:8080";
    private static final String STOCK_API_PATH = "/api/stocks";

    /**
     * 여러 상품의 재고 조회
     * @param productIds 조회할 상품 ID 목록
     * @return 재고 정보 목록
     */
    public List<StockResponse> searchStocks(List<Long> productIds) {
        try {
            StockSearchRequest request = new StockSearchRequest(productIds);

            URI uri = UriComponentsBuilder
                    .fromUriString(BASE_URL)
                    .path(STOCK_API_PATH + "/search")
                    .encode()
                    .build()
                    .toUri();

            ResponseEntity<List<StockResponse>> response = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    new ParameterizedTypeReference<List<StockResponse>>() {}
            );

            log.info("재고 조회 성공 - 요청 상품 수: {}, 응답 수: {}",
                    productIds.size(), response.getBody().size());

            return response.getBody();
        } catch (Exception e) {
            log.error("재고 조회 실패 - productIds: {}, error: {}", productIds, e.getMessage());
            throw new RuntimeException("재고 조회 중 오류 발생", e);
        }
    }

    /**
     * 단일 상품 재고 조회
     * @param productId 조회할 상품 ID
     * @return 재고 정보
     */
    public StockResponse getStock(Long productId) {
        try {
            URI uri = UriComponentsBuilder
                    .fromUriString(BASE_URL)
                    .path(STOCK_API_PATH + "/product/{productId}")
                    .buildAndExpand(productId)
                    .toUri();

            ResponseEntity<StockResponse> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    null,
                    StockResponse.class
            );

            log.info("단일 재고 조회 성공 - productId: {}, 재고: {}",
                    productId, Objects.requireNonNull(response.getBody()).getQuantity());

            return response.getBody();
        } catch (Exception e) {
            log.error("단일 재고 조회 실패 - productId: {}, error: {}", productId, e.getMessage());
            throw new RuntimeException("재고 조회 중 오류 발생", e);
        }
    }

    /**
     * 재고 충분 여부 확인
     * @param productId 상품 ID
     * @param requiredQuantity 필요 수량
     * @return 재고 충분 여부
     */
    public boolean hasEnoughStock(Long productId, int requiredQuantity) {
        try {
            StockResponse stock = getStock(productId);
            return stock.getQuantity() >= requiredQuantity;
        } catch (Exception e) {
            log.error("재고 확인 실패 - productId: {}, required: {}", productId, requiredQuantity);
            return false;
        }
    }

    /**
     * 여러 상품의 재고 충분 여부 확인
     * @param productQuantityMap 상품 ID-수량 맵
     * @return 모든 상품의 재고가 충분한지 여부
     */
    public boolean hasEnoughStockForAll(java.util.Map<Long, Integer> productQuantityMap) {
        List<Long> productIds = new java.util.ArrayList<>(productQuantityMap.keySet());
        List<StockResponse> stocks = searchStocks(productIds);

        return stocks.stream()
                .allMatch(stock -> {
                    Integer required = productQuantityMap.get(stock.getProductId());
                    return required != null && stock.getQuantity() >= required;
                });
    }
}