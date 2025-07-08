package com.example.hotdeal.domain.product.product.application;

import com.example.hotdeal.domain.product.product.domain.Product;
import com.example.hotdeal.domain.product.product.domain.dto.ProductCreateRequest;
import com.example.hotdeal.domain.product.product.domain.dto.ProductCreateResponse;

public interface ProductService {
    ProductCreateResponse createProduct(ProductCreateRequest request);
    Product findById(Long productId);
    void deleteProduct(Long productId);
}
