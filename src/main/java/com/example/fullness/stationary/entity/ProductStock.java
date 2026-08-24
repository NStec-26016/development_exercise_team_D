package com.example.fullness.stationary.entity;

import lombok.Data;

@Data
public class ProductStock {
    private Integer id;
    private Integer productId;
    private Integer quantity;
}