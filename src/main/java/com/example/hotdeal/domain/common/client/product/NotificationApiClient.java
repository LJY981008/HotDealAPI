package com.example.hotdeal.domain.common.client.product;

import com.example.hotdeal.domain.user.subscribe.domain.SubscribeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationApiClient {

    private final RestTemplate restTemplate;

    public List<SubscribeResponse> searchUserFromSubscribeToProduct(Long productId) {
        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:8080")
                .path("/api/subscribe/search-sub-user")
                .queryParam("productId", productId)
                .encode()
                .build()
                .toUri();

        ResponseEntity<List<SubscribeResponse>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<SubscribeResponse>>() {}
        );
        return response.getBody();
    }
}
