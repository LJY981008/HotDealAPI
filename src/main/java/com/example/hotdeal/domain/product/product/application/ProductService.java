package com.example.hotdeal.domain.product.product.application;

import com.example.hotdeal.domain.product.product.domain.AddEventRequest;
import com.example.hotdeal.domain.product.product.domain.AddEventResponse;
import com.example.hotdeal.domain.product.product.infra.ProductRepository;
import com.example.hotdeal.global.enums.CustomErrorCode;
import com.example.hotdeal.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public List<AddEventResponse> addEvent(List<Long> productIds, Long eventId) {
        long updatedCount = productRepository.updateProductEventIds(productIds, eventId);
        if (updatedCount <= 0) {
            throw new CustomException(CustomErrorCode.NOT_FOUND_PRODUCT);
        }
        return productRepository.findProductsByIds(productIds);
    }
}
