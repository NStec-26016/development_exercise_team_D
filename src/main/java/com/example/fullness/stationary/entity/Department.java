package com.example.fullness.stationary.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class Department implements Serializable {

    private Integer id;

    // Nameをnameに変更（UC９）
    private String name;
}