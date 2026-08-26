package com.example.fullness.stationary.entity;

import java.io.Serializable;
import lombok.Data;

@Data
public class Category implements Serializable {
    private Integer id;
    private String name;
}