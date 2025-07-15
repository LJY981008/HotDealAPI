package com.example.hotdeal.domain.product.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hotdeal.domain.product.domain.Product;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {
}