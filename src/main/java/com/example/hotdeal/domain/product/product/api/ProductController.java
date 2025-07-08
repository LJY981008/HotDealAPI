package com.example.hotdeal.domain.product.product.api;

import com.example.hotdeal.domain.product.product.application.ProductService;
import com.example.hotdeal.domain.product.product.domain.AddEventRequest;
import com.example.hotdeal.domain.product.product.domain.AddEventResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PatchMapping("/add-event")
    public ResponseEntity<List<AddEventResponse>> addEvent(
            @RequestParam Long eventId,
            @RequestBody AddEventRequest request
    ){
        List<AddEventResponse> addEventResponses = productService.addEvent(request, eventId);
        return ResponseEntity.status(HttpStatus.OK).body(addEventResponses);
    }
}
