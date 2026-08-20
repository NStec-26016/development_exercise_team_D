package com.example.fullness.stationary.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class Product implements Serializable {
    private Integer id;         
    private String name;        
    private BigDecimal price;   
    private String imagePath;   
    private Integer categoryId;
    private Integer stock;      // ★ 追加
}