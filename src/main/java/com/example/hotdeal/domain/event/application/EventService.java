package com.example.hotdeal.domain.event.application;

import com.example.hotdeal.domain.event.domain.Event;
import com.example.hotdeal.domain.event.domain.EventCrateRequest;
import com.example.hotdeal.domain.event.domain.EventResponse;
import com.example.hotdeal.domain.event.infra.EventRepository;
import jakarta.validation.Valid;
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
}
