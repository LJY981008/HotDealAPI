package com.example.hotdeal.domain.event.api;

import com.example.hotdeal.domain.event.application.EventService;
import com.example.hotdeal.domain.event.domain.EventAddProductRequest;
import com.example.hotdeal.domain.event.domain.EventCrateRequest;
import com.example.hotdeal.domain.event.domain.EventResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/add-product")
    public ResponseEntity<EventResponse> addEventToProduct(
            @RequestParam Long eventId,
            @Valid @RequestBody EventAddProductRequest request
    ) {
        EventResponse eventResponse = eventService.addEventToProduct(eventId, request);
        return ResponseEntity.status(HttpStatus.OK).body(eventResponse);
    }

    @DeleteMapping("/remove-product")
    public ResponseEntity<EventResponse> removeEventFromProduct(
            @RequestParam Long eventId,
            @RequestParam Long productId
    ) {
        EventResponse eventResponse = eventService.removeEventFromProduct(eventId, productId);
        return ResponseEntity.status(HttpStatus.OK).body(eventResponse);
    }
}
