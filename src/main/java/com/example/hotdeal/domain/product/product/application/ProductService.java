package com.example.hotdeal.domain.product.product.application;

import com.example.hotdeal.domain.product.product.domain.Product;
import com.example.hotdeal.domain.product.product.domain.dto.CreateProductRequest;
import com.example.hotdeal.domain.product.product.domain.dto.CreateProductResponse;
import com.example.hotdeal.domain.product.product.domain.dto.SearchProductResponse;

import java.util.List;

public interface ProductService {
    CreateProductResponse createProduct(CreateProductRequest request);
    Product findById(Long productId);
    void deleteProduct(Long productId);
    List<SearchProductResponse> searchProductsByIds(List<Long> productIds);
}
