package com.example.fullness.stationary.entity;

import java.io.Serializable;
import lombok.Data;

@Data
public class ProductCategory implements Serializable {
    private Integer id; // HTMLの ${cat.id} に合わせる
    private String name; // HTMLの ${cat.name} に合わせる
}
