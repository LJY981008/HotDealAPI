package com.example.hotdeal.domain.event.api;

import com.example.hotdeal.domain.event.application.EventService;
import com.example.hotdeal.domain.event.domain.dto.EventAddProductRequest;
import com.example.hotdeal.domain.event.domain.dto.EventCrateRequest;
import com.example.hotdeal.domain.event.domain.dto.EventResponse;
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
}
