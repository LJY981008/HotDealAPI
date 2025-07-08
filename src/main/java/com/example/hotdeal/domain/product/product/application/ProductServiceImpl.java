package com.example.hotdeal.domain.product.product.application;

import com.example.hotdeal.domain.product.product.domain.command.Product;
import com.example.hotdeal.domain.product.product.domain.command.dto.ProductCreateRequest;
import com.example.hotdeal.domain.product.product.domain.command.dto.ProductCreateResponse;
import com.example.hotdeal.domain.product.product.exception.ProductException;
import com.example.hotdeal.domain.product.product.infra.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ProductCreateResponse createProduct(ProductCreateRequest request) {
        Product product = request.toProduct();
        Product savedProduct = productRepository.save(product);
        return new ProductCreateResponse(savedProduct);
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