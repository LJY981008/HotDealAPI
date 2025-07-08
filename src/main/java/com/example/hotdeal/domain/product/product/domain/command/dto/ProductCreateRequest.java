package com.example.hotdeal.domain.product.product.domain.command.dto;

import com.example.hotdeal.domain.product.product.domain.command.Product;
import com.example.hotdeal.domain.product.product.domain.command.ProductCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProductCreateRequest {

    @NotBlank(message = "상품명은 필수입니다")
    private String productName;

    @NotBlank(message = "상품 설명은 필수입니다")
    private String productDescription;

    @NotNull(message = "상품 가격은 필수입니다")
    @DecimalMin(value = "0.0", inclusive = false, message = "상품 가격은 0보다 커야 합니다")
    private BigDecimal productPrice;

    @NotBlank(message = "상품 이미지 URL은 필수입니다")
    private String productImageUrl;

    @NotNull(message = "상품 카테고리는 필수입니다")
    private ProductCategory productCategory;

    public Product toProduct() {
        return new Product(
                this.productName,
                this.productDescription,
                this.productPrice,
                this.productImageUrl,
                this.productCategory
        );
    }
}
