package com.example.hotdeal.domain.order.domain;

import java.math.BigDecimal;

import com.example.hotdeal.global.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "order_items")
public class OrderItem extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "item_id")
	private Long itemId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;

	@Column(name = "product_id")
	private Long productId;

	@NotNull
	@Column(name = "product_name")
	private String productName;

	@Column(name = "order_item_count")
	private int orderItemCount;

	@Column(name = "order_item_total_price")
	private BigDecimal itemTotalPrice;

	public OrderItem() {
	}

	public OrderItem(Long productId, String productName, int orderItemCount, BigDecimal itemTotalPrice) {
		this.productId = productId;
		this.productName = productName;
		this.orderItemCount = orderItemCount;
		this.itemTotalPrice = itemTotalPrice;
	}

}