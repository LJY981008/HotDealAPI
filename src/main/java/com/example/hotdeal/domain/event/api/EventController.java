package com.example.hotdeal.domain.event.api;

import com.example.hotdeal.domain.common.client.event.dto.EventProductResponse;
import com.example.hotdeal.domain.event.application.EventService;
import com.example.hotdeal.domain.event.domain.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/create")
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody EventCrateRequest request
    ){
        EventResponse event = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    @PostMapping("/search-event")
    public ResponseEntity<List<EventProductResponse>> getEvents(
            @RequestBody SearchEventToProductIdRequest request
    ) {
        List<EventProductResponse> responses = eventService.getEvent(request.getProductIds());
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }
}
