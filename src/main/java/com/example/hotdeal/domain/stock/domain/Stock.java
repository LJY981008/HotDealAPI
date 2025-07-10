package com.example.hotdeal.domain.stock.domain;
import com.example.hotdeal.global.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "stock")
@NoArgsConstructor(access = PROTECTED)
public class Stock extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;

    private int quantity; //실제 재고


    public Stock(Long productId, int quantity) {
        this.productId = productId;
        this.quantity  = quantity;
    }

    public Stock(int quantity) {this.quantity = quantity;}

    // 비즈니스 메서드
    public void decrease(int quantity) {
        if (this.quantity < quantity) {
            throw new IllegalStateException("재고 부족");
        }
        this.quantity -= quantity;
    }
    public void increase(int quantity)        { this.quantity += quantity; }
    public void reset(int quantity)      { this.quantity = quantity; }

}
