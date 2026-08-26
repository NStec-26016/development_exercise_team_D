package com.example.fullness.stationary.entity;

import java.io.Serializable;
import lombok.Data;

@Data
public class Product implements Serializable {
    private Integer id;
    private Integer productCategoryId;

    private String name;

    private Integer price;

    // UC10追加
    private String imageUrl;

    // UC10追加
    private Integer deleteFlag;

    private Integer stock; // ★ 追加
}
