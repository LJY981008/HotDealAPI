package com.example.hotdeal;

import com.example.hotdeal.domain.product.domain.Product;
import com.example.hotdeal.domain.product.domain.ProductCategory;
import com.example.hotdeal.domain.product.infra.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootTest
@ActiveProfiles("test")
@WithMockUser(username = "testuser", roles = {"USER"})
public class ProductDataInsertTest {

    @Autowired
    private ProductRepository productRepository;

    private final Random random = new Random();

    @Test
    public void insertDummyProducts() {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            String name = "Product-" + i;
            String description = "Description for product " + i;
            BigDecimal price = BigDecimal.valueOf(1000 + random.nextInt(9000));
            String imageUrl = "https://dummyimage.com/600x400/000/fff&text=Product+" + i;
            ProductCategory category = ProductCategory.values()[random.nextInt(ProductCategory.values().length)];
            Product product = new Product(name, description, price, imageUrl, category);
            products.add(product);
        }
        productRepository.saveAll(products);
    }
} 