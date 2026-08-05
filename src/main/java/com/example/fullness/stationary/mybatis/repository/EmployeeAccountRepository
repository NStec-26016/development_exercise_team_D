package com.example.fullness.stationary.mybatis.repository;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.fullness.stationary.mybatis.entity.EmployeeAccount;

@Mapper
@Repository

public interface EmployeeAccountRepository {
    // UC17 担当者ログイン
    EmployeeAccount findByName(@Param("name") String name);

    // UC09 担当者アカウント登録
    int insertEmployeeAccount(EmployeeAccount employeeAccount);
}

