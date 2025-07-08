package com.example.hotdeal.domain.event.application;

import com.example.hotdeal.domain.event.domain.ProductEventAddedEvent;
import com.example.hotdeal.domain.event.domain.entity.Event;
import com.example.hotdeal.domain.event.domain.dto.EventAddProductRequest;
import com.example.hotdeal.domain.event.domain.dto.EventCrateRequest;
import com.example.hotdeal.domain.event.domain.dto.EventResponse;
import com.example.hotdeal.domain.event.infra.EventRepository;
import com.example.hotdeal.global.enums.CustomErrorCode;
import com.example.hotdeal.global.event.model.EventPublisher;
import com.example.hotdeal.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventPublisher eventPublisher;

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


        eventPublisher.publish(new ProductEventAddedEvent(eventId, request.getProductIds()));

        return new EventResponse(event);
    }

    @Transactional
    public EventResponse removeEventFromProduct(Long eventId, Long productId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_EVENT));

        return new EventResponse(event);
    }
}
