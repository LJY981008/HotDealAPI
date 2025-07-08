package com.example.hotdeal.domain.product.product.infra;

import com.example.hotdeal.domain.product.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
}
