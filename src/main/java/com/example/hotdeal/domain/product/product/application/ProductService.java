package com.example.hotdeal.domain.product.product.application;

import com.example.hotdeal.domain.product.product.domain.AddEventResponse;
import com.example.hotdeal.domain.product.product.domain.Product;
import com.example.hotdeal.domain.product.product.infra.ProductRepository;
import com.example.hotdeal.global.enums.CustomErrorCode;
import com.example.hotdeal.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public AddEventResponse addEvent(Long productId, Long eventId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_PRODUCT));
        product.addEvent(eventId);

        Product save = productRepository.save(product);
        return new AddEventResponse(save);
    }
}
