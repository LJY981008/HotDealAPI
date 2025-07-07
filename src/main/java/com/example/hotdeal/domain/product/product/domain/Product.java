package com.example.hotdeal.domain.product.product.domain;

import com.example.hotdeal.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "products")
public class Product extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private Long product_event_id;
    private String product_name;
    private String product_description;
    private String product_price;
    private String product_image_url;
    private String product_category;

}
