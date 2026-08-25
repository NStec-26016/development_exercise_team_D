package com.example.fullness.stationary.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class Product implements Serializable {
    private Integer id;
    // UC10追加
    private Integer productCategoryId;

    private String name;

    private Integer price;

    // UC10追加
    private String imageUrl;

    // UC10追加
    private Integer deleteFlag;

    private Integer stock; // ★ 追加
}
