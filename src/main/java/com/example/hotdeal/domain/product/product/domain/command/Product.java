package com.example.hotdeal.domain.product.product.domain.command;

import com.example.hotdeal.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "products")
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "product_description", columnDefinition = "TEXT")
    private String productDescription;

    @Column(name = "product_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal productPrice;

    @Column(name = "product_image_url", length = 500)
    private String productImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_category", length = 50)
    private ProductCategory productCategory;

    // 생성자
    public Product(String productName, String productDescription, BigDecimal productPrice,
                   String productImageUrl, ProductCategory productCategory) {
        this.productName = productName;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
        this.productImageUrl = productImageUrl;
        this.productCategory = productCategory;

    }

    // 상품 정보 업데이트
    public void updateProduct(String productName, String productDescription,
                              BigDecimal productPrice, String productImageUrl, ProductCategory productCategory) {
        if (productName != null) {
            this.productName = productName;
        }
        if (productDescription != null) {
            this.productDescription = productDescription;
        }
        if (productPrice != null) {
            this.productPrice = productPrice;
        }
        if (productImageUrl != null) {
            this.productImageUrl = productImageUrl;
        }
        if (productCategory != null) {
            this.productCategory = ProductCategory.validateAndGet(productCategory);
        }
    }
}
