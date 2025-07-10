package com.example.hotdeal.domain.product.product.api;

import com.example.hotdeal.domain.product.product.application.ProductService;
import com.example.hotdeal.domain.product.product.domain.dto.SearchProductListRequest;
import com.example.hotdeal.domain.product.product.domain.dto.SearchProductResponse;
import com.example.hotdeal.domain.product.product.domain.Product;
import com.example.hotdeal.domain.product.product.domain.dto.CreateProductRequest;
import com.example.hotdeal.domain.product.product.domain.dto.CreateProductResponse;
import com.example.hotdeal.domain.product.product.domain.dto.UpdateProductRequest;
import com.example.hotdeal.domain.product.product.domain.dto.UpdateProductResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 이벤트용 상품 다건 조회 API
    @PostMapping("/search-product")
    public ResponseEntity<List<SearchProductResponse>> searchProducts(
            @Valid @RequestBody SearchProductListRequest request
    ) {
        List<SearchProductResponse> products = productService.searchProductsByIds(request.getProductIds());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<SearchProductResponse> getProduct(
            @PathVariable Long productId
    ) {
        Product product = productService.findById(productId);
        SearchProductResponse response = new SearchProductResponse(product);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CreateProductResponse> createProduct(
            @Valid @RequestBody CreateProductRequest request
    ) {
        CreateProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<UpdateProductResponse> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        Product product = productService.findById(productId);

        product.updateProduct(
                request.productName(),
                request.productDescription(),
                request.productPrice(),
                request.productImageUrl(),
                request.productCategory()
        );

        return ResponseEntity.ok(new UpdateProductResponse(product));
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long productId
    ) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
