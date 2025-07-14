package com.example.hotdeal.domain.product.application;

import java.util.List;

import com.example.hotdeal.domain.common.client.product.dto.SearchProductResponse;
import com.example.hotdeal.domain.product.domain.Product;
import com.example.hotdeal.domain.product.domain.dto.CreateProductRequest;
import com.example.hotdeal.domain.product.domain.dto.CreateProductResponse;

public interface ProductService {

	CreateProductResponse createProduct(CreateProductRequest request);

	Product findById(Long productId);

	void deleteProduct(Long productId);

	List<SearchProductResponse> searchProductsByIds(List<Long> productIds);

}