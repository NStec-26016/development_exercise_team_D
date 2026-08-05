package com.example.fullness.stationary.mybatis.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class Employee implements Serializable{

    private Integer id;

    private Integer departmentId;

    private String name;

    private String nameKana;

    private String password;
}