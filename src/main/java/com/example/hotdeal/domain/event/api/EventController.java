package com.example.hotdeal.domain.event.api;

import com.example.hotdeal.domain.common.client.event.dto.EventProductResponse;
import com.example.hotdeal.domain.event.application.EventService;
import com.example.hotdeal.domain.event.domain.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/create")
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody EventCrateRequest request
    ){
        long startTime = System.currentTimeMillis();
        EventResponse event = eventService.createEvent(request);
        log.info("8단계 - 컨트롤러 실행: {}ms", System.currentTimeMillis() - startTime);
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
