package com.example.fullness.stationary.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class ProductStock implements Serializable {

    private Integer id;

    private Integer productId;

    private Integer quantity;
}