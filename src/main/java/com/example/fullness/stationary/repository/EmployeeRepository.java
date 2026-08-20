package com.example.fullness.stationary.repository;

import com.example.fullness.stationary.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Optional;

@Mapper
public interface EmployeeRepository {

    // 1. アカウント未登録の社員一覧をID順で取得
    List<Employee> findUnregisteredEmployees();

    // 2. 主キー（ID）で社員情報を1件検索
    Optional<Employee> findById(Integer id);
}
