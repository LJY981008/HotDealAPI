package com.example.hotdeal.domain.common.client.product;

import java.net.URI;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.hotdeal.domain.common.client.product.dto.SearchProductResponse;
import com.example.hotdeal.global.enums.CustomErrorCode;
import com.example.hotdeal.global.exception.CustomException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ProductApiClient {

	private final RestTemplate restTemplate;
	private final String PRODUCT_API_BASE_URL;

	public ProductApiClient(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		this.PRODUCT_API_BASE_URL = "http://localhost:8080/api/products";
	}

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
		log.info("상품 다건 조회 - productIds: {}", productIds);

		try {
			URI uri = UriComponentsBuilder
				.fromUriString(PRODUCT_API_BASE_URL)
				.path("/search-product")
				.encode()
				.build()
				.toUri();

			ProductSearchRequest request = new ProductSearchRequest(productIds);

			ResponseEntity<List<SearchProductResponse>> response = restTemplate.exchange(
				uri,
				HttpMethod.POST,
				new HttpEntity<>(request),
				new ParameterizedTypeReference<List<SearchProductResponse>>() {
				}
			);

			return response.getBody();
		} catch (Exception e) {
			log.error("상품 다건 조회 실패: {}", e.getMessage());
			throw new CustomException(CustomErrorCode.FAILED_CALL_API);
		}
	}

	private record ProductSearchRequest(List<Long> productIds) {
	}

}