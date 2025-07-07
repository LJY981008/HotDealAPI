package com.example.hotdeal.domain.event.application;

import com.example.hotdeal.domain.event.domain.Event;
import com.example.hotdeal.domain.event.domain.EventAddProductRequest;
import com.example.hotdeal.domain.event.domain.EventCrateRequest;
import com.example.hotdeal.domain.event.domain.EventResponse;
import com.example.hotdeal.domain.event.infra.EventRepository;
import com.example.hotdeal.global.enums.CustomErrorCode;
import com.example.hotdeal.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public EventResponse createEvent(EventCrateRequest request) {
        Event event = request.toEvent();
        Event savedEvent = eventRepository.save(event);

        return new EventResponse(savedEvent);
    }

    public EventResponse addEventToProduct(Long eventId, EventAddProductRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_EVENT));
        event.addEventToProduct(request.getProductIds());

        return new EventResponse(event);
    }

    public EventResponse removeEventFromProduct(Long eventId, Long productId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_EVENT));
        event.removeEventFromProduct(productId);

        return new EventResponse(event);
    }
}
