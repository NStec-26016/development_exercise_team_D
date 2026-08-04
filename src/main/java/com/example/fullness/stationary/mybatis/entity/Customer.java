package com.example.fullness.stationary.mybatis.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Customer implements Serializable{
    
    private String name;

    private String nameKana;

    private String address1;

    private String address2;

    private String phoneNumber;
    
    private String mailAddress;

    private String username;

    private String password;

    private LocalDateTime registerDate;

}