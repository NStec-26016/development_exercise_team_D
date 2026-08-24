package com.example.fullness.stationary.repository;

import com.example.fullness.stationary.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface EmployeeRepository {
    // XMLで定義したSQLを実行するメソッド
    List<Employee> findUnregisteredEmployees();
}
