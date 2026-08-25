package com.example.fullness.stationary.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class Product implements Serializable {
    private Integer id;
    // UC10追加
    private Integer product_category_id;

    private String name;

    private Integer price;

    // private String imagePath;

    // UC10追加
    private String imageUrl;

    // ここを private Integer categoryId;

    // UC10追加
    private Integer delete_flag;

    private Integer stock; // ★ 追加
}
