package com.example.hotdeal.domain.common.client.event;

import java.net.URI;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.hotdeal.domain.common.client.event.dto.EventProductResponse;
import com.example.hotdeal.global.enums.CustomErrorCode;
import com.example.hotdeal.global.exception.CustomException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class HotDealApiClient {

	private final RestTemplate restTemplate;
	private final String baseUrl;

	public HotDealApiClient(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		this.baseUrl = "http://localhost:8080";
	}

	public List<EventProductResponse> getEvents(List<Long> productIds) {
		log.info("이벤트 API 호출 시작 - productIds: {}", productIds);

		return callApi(
			"/api/event/search-event",
			new restRequestProductIds(productIds),
			new ParameterizedTypeReference<List<EventProductResponse>>() {
			}
		);
	}

	private <T, R> R callApi(
		String path,
		T request,
		ParameterizedTypeReference<R> responseType
	) {
		try {
			URI uri = UriComponentsBuilder
				.fromUriString(baseUrl)
				.path(path)
				.encode()
				.build()
				.toUri();

			ResponseEntity<R> response = restTemplate.exchange(
				uri, HttpMethod.POST, new HttpEntity<>(request), responseType
			);

			return response.getBody();
		} catch (Exception e) {
			log.error("API 호출 실패: {} - {}", path, e.getMessage());
			throw new CustomException(CustomErrorCode.FAILED_CALL_API);
		}
	}

	private record restRequestProductIds(List<Long> productIds) {
	}

}