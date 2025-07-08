package com.example.hotdeal.domain.product.product.infra;

import com.example.hotdeal.domain.product.product.domain.command.Product;

import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(Long productId);
    void delete(Product product);
}
