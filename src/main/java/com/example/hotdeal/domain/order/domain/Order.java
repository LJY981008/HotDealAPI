package com.example.hotdeal.domain.order.domain;

import com.example.hotdeal.domain.order.enums.OrderStatus;
import com.example.hotdeal.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    private Long userId;

    @Column(name = "order_total_count")
    private Integer orderTotalCount;

    @Column(name = "order_total_price")
    private BigDecimal orderTotalPrice;

    @Column(name = "order_item_ids", columnDefinition = "json")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus = OrderStatus.ORDER_BEFORE;

    @Column(name = "order_time")
    private LocalDateTime orderTime;

    public Order() {}

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }


    public Order(Long userId,
                 BigDecimal orderTotalPrice,
                 Integer orderTotalCount) {
        this.userId = userId;
        this.orderTotalPrice = orderTotalPrice;
        this.orderTotalCount = orderTotalCount;
    }
}
