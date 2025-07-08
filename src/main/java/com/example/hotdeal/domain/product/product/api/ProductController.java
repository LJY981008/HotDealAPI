package com.example.hotdeal.domain.product.product.api;

import com.example.hotdeal.domain.product.product.application.ProductService;
import com.example.hotdeal.domain.product.product.domain.SearchProductListRequest;
import com.example.hotdeal.domain.product.product.domain.SearchProductResponse;
import jakarta.validation.Valid;
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

    @GetMapping("/search-product")
    public ResponseEntity<List<SearchProductResponse>> getProduct(
            @Valid @RequestBody SearchProductListRequest searchProductListRequest
    ){
        List<SearchProductResponse> searchProductResponse = productService.getProduct(searchProductListRequest);
        return ResponseEntity.status(HttpStatus.OK).body(searchProductResponse);
    }
}
