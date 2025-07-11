package com.example.hotdeal.domain.product.infra;

import com.example.hotdeal.domain.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {
}