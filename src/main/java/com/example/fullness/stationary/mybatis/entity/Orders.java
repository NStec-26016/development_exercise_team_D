package com.example.fullness.stationary.mybatis.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Orders implements Serializable{
    
    private Integer customerId;

    private Integer orderStatusId;

    private Integer paymentMethodId;

    private LocalDateTime orderDate;

    private Integer amountTotal;
    

}