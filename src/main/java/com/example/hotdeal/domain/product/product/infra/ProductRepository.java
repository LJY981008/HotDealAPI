package com.example.hotdeal.domain.product.product.infra;


import com.example.hotdeal.domain.product.product.domain.command.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>{
    List<Product> findByProductIdIn(List<Long> productIds);
}
