package com.example.hotdeal.domain.product.infra;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.hotdeal.domain.product.domain.Product;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

	private final ProductJpaRepository productJpaRepository;

	@Override
	public List<Product> findAllByIds(List<Long> productIds) {
		return productJpaRepository.findAllById(productIds)
			.stream()
			.filter(product -> !product.isDeleted()) // 삭제되지 않은 상품만
			.collect(Collectors.toList());
	}

	@Override
	public Product save(Product product) {
		return productJpaRepository.save(product);
	}

	@Override
	public List<Product> saveAll(List<Product> products) {
		return productJpaRepository.saveAll(products);
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