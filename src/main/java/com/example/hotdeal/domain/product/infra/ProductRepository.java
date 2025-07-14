package com.example.hotdeal.domain.product.infra;

import java.util.List;
import java.util.Optional;

import com.example.hotdeal.domain.product.domain.Product;

public interface ProductRepository {

	Product save(Product product);

	List<Product> saveAll(List<Product> products);

	Optional<Product> findById(Long productId);

	void delete(Product product);

	List<Product> findAllByIds(List<Long> productIds);

}