package com.example.fullness.stationary.mybatis.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class Department implements Serializable{
    
    private Integer id;

    private String Name;
}