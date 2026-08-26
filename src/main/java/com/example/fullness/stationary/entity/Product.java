package com.example.fullness.stationary.entity;

import java.io.Serializable;
import lombok.Data;

@Data
public class Product implements Serializable {
    private Integer id;
    private String name;
    private Integer price;
    private String imageUrl;
    private Integer productCategoryId;
    private Integer deleteFlag;

}