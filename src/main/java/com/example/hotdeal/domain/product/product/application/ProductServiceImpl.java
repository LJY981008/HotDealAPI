package com.example.hotdeal.domain.product.product.application;

import com.example.hotdeal.domain.product.product.domain.Product;
import com.example.hotdeal.domain.product.product.domain.dto.CreateProductRequest;
import com.example.hotdeal.domain.product.product.domain.dto.CreateProductResponse;
import com.example.hotdeal.domain.product.product.domain.dto.SearchProductResponse;
import com.example.hotdeal.domain.product.product.exception.ProductException;
import com.example.hotdeal.domain.product.product.infra.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SearchProductResponse> searchProductsByIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return List.of();
        }

        return productRepository.findAllByIds(productIds)
                .stream()
                .map(SearchProductResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CreateProductResponse createProduct(CreateProductRequest request) {
        Product product = request.toProduct();
        Product savedProduct = productRepository.save(product);
        return new CreateProductResponse(savedProduct);
    }

    @Override
    public Product findById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(ProductException::productIdNotFound);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = findById(productId);
        product.softDelete();
    }
}