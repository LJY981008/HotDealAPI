package com.example.hotdeal.domain.order.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    ORDER_SUCCESS,  // 주문 완료
    ORDER_PENDING,  // 주문 대기
    ORDER_FAILURE,  // 주문 실패
    ORDER_BEFORE    // 주문 전
}
