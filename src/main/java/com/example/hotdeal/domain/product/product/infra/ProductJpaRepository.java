package com.example.hotdeal.domain.product.product.infra;

import com.example.hotdeal.domain.product.product.domain.command.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {
}