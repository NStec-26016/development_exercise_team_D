package com.example.fullness.stationary.mybatis.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class ProductStock implements Serializable{
    
    private Integer productId;

    private Integer quantity;
    

}