package com.example.fullness.stationary.repository;

import com.example.fullness.stationary.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param; // ★忘れず追加
import java.util.List;

@Mapper
public interface EmployeeRepository {
    // 既存メソッド
    List<Employee> findUnregisteredEmployees();

    /**
     * 社員IDから名前を取得
     * @Param("id") を付けることで、XMLの #{id} と確実に紐付けます。
     */
    String findNameById(@Param("id") int id);
}
