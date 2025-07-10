package com.example.hotdeal.domain.order.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemDto {
    private final Long productId;
    private final String productName;
    private Integer quantity;
    private final BigDecimal productPrice;
    private BigDecimal itemTotalPrice;


    public OrderItemDto(Long productId, String productName, BigDecimal productPrice) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.itemTotalPrice = BigDecimal.ZERO;
        this.quantity = 0;
    }

    public void updateItemTotalPrice(BigDecimal itemTotalPrice){
        this.itemTotalPrice = itemTotalPrice;
    }

    public void updateItemQuantities(Integer quantity) {
        this.quantity = quantity;
    }
}
