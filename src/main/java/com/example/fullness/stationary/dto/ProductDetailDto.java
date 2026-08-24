package com.example.fullness.stationary.dto;

import lombok.Data;

@Data
public class ProductDetailDto {
    private Integer id;
    private String name;
    private Integer price;
    private Integer stock;
    private Integer categoryName;
    private String imagePath;

}
