package com.example.fullness.stationary.mybatis.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class OrderDetail implements Serializable{
    private Integer id;

    private Integer orderId;

    private Integer productId;

    private Integer customerId;

    private Integer count;

}