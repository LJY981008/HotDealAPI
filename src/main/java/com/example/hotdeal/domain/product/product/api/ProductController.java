package com.example.hotdeal.domain.product.product.api;

import com.example.hotdeal.domain.product.product.application.ProductService;
import com.example.hotdeal.domain.product.product.domain.AddEventResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PatchMapping("/add-event")
    public ResponseEntity<AddEventResponse> addEvent(
            @RequestParam Long productId,
            @RequestParam Long eventId
    ){
        AddEventResponse addEventResponse = productService.addEvent(productId, eventId);
        return ResponseEntity.status(HttpStatus.OK).body(addEventResponse);
    }
}
