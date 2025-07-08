package com.example.hotdeal.domain.product.product.application;

import com.example.hotdeal.domain.product.product.domain.SearchProductListRequest;
import com.example.hotdeal.domain.product.product.domain.SearchProductResponse;
import com.example.hotdeal.domain.product.product.domain.command.Product;
import com.example.hotdeal.domain.product.product.infra.ProductRepository;
import com.example.hotdeal.global.enums.CustomErrorCode;
import com.example.hotdeal.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public List<SearchProductResponse> getProduct(SearchProductListRequest productListRequest) {
        List<Long> productIds = productListRequest.getProductIds();
        List<Product> products = productRepository.findByProductIdIn(productIds);

        return products.stream().map(SearchProductResponse::new).collect(Collectors.toList());
    }

}
