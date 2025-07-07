package com.example.hotdeal.domain.inventory.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "inventorys")
public class Inventory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;

    private int itemCount;
    private LocalDateTime hotTime;
}
