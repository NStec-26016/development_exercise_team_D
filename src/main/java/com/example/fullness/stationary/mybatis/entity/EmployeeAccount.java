package com.example.fullness.stationary.mybatis.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EmployeeAccount implements Serializable {
    private Integer id;

    private Integer employeeId;

    private String name;

    private String password;

    private String employeeAccountRole;
}
