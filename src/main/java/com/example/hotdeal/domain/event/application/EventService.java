package com.example.hotdeal.domain.event.application;

import com.example.hotdeal.domain.event.domain.dto.WSEventProduct;
import com.example.hotdeal.domain.event.domain.entity.Event;
import com.example.hotdeal.domain.event.domain.dto.EventAddProductRequest;
import com.example.hotdeal.domain.event.domain.dto.EventCrateRequest;
import com.example.hotdeal.domain.event.domain.dto.EventResponse;
import com.example.hotdeal.domain.event.domain.entity.EventItem;
import com.example.hotdeal.domain.event.infra.EventRepository;
import com.example.hotdeal.domain.product.product.domain.dto.SearchProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RestTemplate restTemplate;

    /**
     * 이벤트 생성
     *
     * @param request 요청값
     */
    @Transactional
    public EventResponse createEvent(EventCrateRequest request) {
        Event event = eventRepository.save(request.toEvent());
        List<SearchProductResponse> searchProductResponses = productSearch(request.getProductIds());
        List<EventItem> eventItems = searchProductResponses.stream().map(response -> new EventItem(response, event.getEventDiscount()))
                .toList();
        event.setProducts(eventItems);

        List<WSEventProduct> wsEventProduct = eventItems.stream()
                .map(eventItem ->
                        new WSEventProduct(
                                eventItem.getEvent().getEventType(),
                                eventItem.getProductName(),
                                eventItem.getOriginalPrice(),
                                eventItem.getDiscountPrice(),
                                eventItem.getEvent().getEventDiscount()
                        )
                )
                .toList();
        eventPublisher.publishEvent(wsEventProduct);

        return new EventResponse(event);
    }

    /**
     * 레스트 템플릿 호출
     *
     * @param productIds
     * @return
     */
    private List<SearchProductResponse> productSearch(List<Long> productIds) {
        EventAddProductRequest eventAddProductRequest = new EventAddProductRequest(productIds);
        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:8080")
                .path("/api/product/search-product")
                .encode()
                .build()
                .toUri();

        ResponseEntity<List<SearchProductResponse>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                new HttpEntity<>(eventAddProductRequest),
                new ParameterizedTypeReference<List<SearchProductResponse>>() {}
        );

        return response.getBody();
    }
}
