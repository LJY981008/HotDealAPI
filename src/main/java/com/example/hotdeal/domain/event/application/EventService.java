package com.example.hotdeal.domain.event.application;

import com.example.hotdeal.domain.event.domain.Event;
import com.example.hotdeal.domain.event.domain.EventAddProductRequest;
import com.example.hotdeal.domain.event.domain.EventCrateRequest;
import com.example.hotdeal.domain.event.domain.EventResponse;
import com.example.hotdeal.domain.event.infra.EventRepository;
import com.example.hotdeal.domain.product.product.domain.AddEventResponse;
import com.example.hotdeal.global.enums.CustomErrorCode;
import com.example.hotdeal.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final RestTemplate restTemplate;

    @Transactional
    public EventResponse createEvent(EventCrateRequest request) {
        Event event = request.toEvent();
        Event savedEvent = eventRepository.save(event);

        return new EventResponse(savedEvent);
    }

    @Transactional
    public EventResponse addEventToProduct(Long eventId, EventAddProductRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_EVENT));
        event.addEventToProduct(request.getProductIds());

        List<AddEventResponse> addEventResponses = publishAddEvent(eventId, request);

        return new EventResponse(event, addEventResponses);
    }

    @Transactional
    public EventResponse removeEventFromProduct(Long eventId, Long productId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_EVENT));
        event.removeEventFromProduct(productId);

        return new EventResponse(event);
    }

    private List<AddEventResponse> publishAddEvent(Long eventId, EventAddProductRequest request) {
        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:8080")
                .path("/api/product/add-event")
                .queryParam("eventId", eventId)
                .encode()
                .build()
                .toUri();
        
        ResponseEntity<List<AddEventResponse>> response = restTemplate.exchange(
            uri,
            HttpMethod.PATCH,
            new HttpEntity<>(request),
            new ParameterizedTypeReference<List<AddEventResponse>>() {}
        );
        
        return response.getBody();
    }
}
