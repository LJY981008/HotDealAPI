package com.example.hotdeal.domain.user.subscribe.api;

import com.example.hotdeal.domain.user.subscribe.application.SubscribeService;

import com.example.hotdeal.domain.user.subscribe.domain.SubscribeRequest;
import com.example.hotdeal.domain.user.subscribe.domain.SubscribeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscribe")
@RequiredArgsConstructor
public class SubscribeController {

    private final SubscribeService subscribeService;

    @PostMapping("/sub-product")
    public ResponseEntity<List<SubscribeResponse>> createSubscribe(
        @Valid @RequestBody SubscribeRequest request
    ){
        List<SubscribeResponse> subscribeResponses = subscribeService.subscribeProduct(request.getUserId(), request.getProductIds());
        return ResponseEntity.status(HttpStatus.CREATED).body(subscribeResponses);
    }

    @GetMapping("/search-sub-user")
    public ResponseEntity<List<SubscribeResponse>> searchSubscribe(
            @RequestParam Long productId
    ) {
        List<SubscribeResponse> subscribeUserByProductId = subscribeService.getSubscribeUserByProductId(productId);
        return ResponseEntity.status(HttpStatus.OK).body(subscribeUserByProductId);
    }

    @DeleteMapping("/cancel-sub")
    public ResponseEntity<String> cancelSubscribe(
            @RequestParam Long userId,
            @RequestParam Long productId
    ) {
        subscribeService.cancelSubscribe(userId, productId);
        return ResponseEntity.status(HttpStatus.OK).body("삭제 성공");
    }
}
