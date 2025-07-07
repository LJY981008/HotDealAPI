package com.example.hotdeal.domain.product.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "products")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
}
