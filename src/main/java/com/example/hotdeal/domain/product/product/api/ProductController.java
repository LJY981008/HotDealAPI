package com.example.hotdeal.domain.product.product.api;

import com.example.hotdeal.domain.product.product.application.ProductService;
import com.example.hotdeal.domain.product.product.domain.command.Product;
import com.example.hotdeal.domain.product.product.domain.command.dto.ProductCreateRequest;
import com.example.hotdeal.domain.product.product.domain.command.dto.ProductCreateResponse;
import com.example.hotdeal.domain.product.product.domain.command.dto.ProductUpdateRequest;
import com.example.hotdeal.domain.product.product.domain.command.dto.ProductUpdateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductCreateResponse> createProduct(
            @Valid @RequestBody ProductCreateRequest request
    ) {
        ProductCreateResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<ProductUpdateResponse> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductUpdateRequest request
    ) {
        Product product = productService.findById(productId);

        product.updateProduct(
                request.productName(),
                request.productDescription(),
                request.productPrice(),
                request.productImageUrl(),
                request.productCategory()
        );

        return ResponseEntity.ok(new ProductUpdateResponse(product));
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
