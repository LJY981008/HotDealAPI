package com.example.hotdeal.domain.user.subscribe.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.hotdeal.domain.user.auth.domain.AuthUserDto;
import com.example.hotdeal.domain.user.subscribe.application.SubscribeService;
import com.example.hotdeal.domain.user.subscribe.domain.SubscribeRequest;
import com.example.hotdeal.domain.user.subscribe.domain.SubscribeResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/subscribe")
@RequiredArgsConstructor
public class SubscribeController {

	private final SubscribeService subscribeService;

	@PostMapping("/sub-product")
	public ResponseEntity<List<SubscribeResponse>> createSubscribe(
		@Valid @RequestBody SubscribeRequest request,
		@AuthenticationPrincipal AuthUserDto user
	) {
		List<SubscribeResponse> subscribeResponses = subscribeService.subscribeProduct(user.getId(),
			request.getProductIds());
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