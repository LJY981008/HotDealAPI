package com.example.hotdeal.domain.product.product.infra;

import com.example.hotdeal.domain.product.product.domain.AddEventResponse;
import com.example.hotdeal.domain.product.product.domain.QAddEventResponse;
import com.example.hotdeal.domain.product.product.domain.QProduct;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public List<AddEventResponse> findProductsByIds(List<Long> ids) {
        QProduct product = QProduct.product;
        return jpaQueryFactory.select(new QAddEventResponse(product))
                .from(product)
                .where(product.productId.in(ids))
                .fetch();
    }

    @Override
    public long updateProductEventIds(List<Long> productIds, Long eventId) {
        QProduct product = QProduct.product;
        return jpaQueryFactory.update(product)
                .set(product.product_event_id, eventId)
                .where(product.productId.in(productIds))
                .execute();
    }
}
