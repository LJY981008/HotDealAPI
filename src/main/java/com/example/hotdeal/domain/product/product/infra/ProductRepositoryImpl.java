package com.example.hotdeal.domain.product.product.infra;

import com.example.hotdeal.domain.product.product.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public Optional<Product> findById(Long productId) {
        return productJpaRepository.findById(productId);
    }

    @Override
    public void delete(Product product) {
        productJpaRepository.delete(product);
    }
}