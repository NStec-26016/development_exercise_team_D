package com.example.fullness.stationary.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.example.fullness.stationary.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // テストごとにデータを綺麗に掃除
        jdbcTemplate.execute("TRUNCATE TABLE employee_account CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE employee CASCADE");

        // 事前条件：社員データを6人登録 (ID1〜6)
        jdbcTemplate
                .execute("INSERT INTO employee (id, department_id, name, name_kana) VALUES (1, 1, '社員一', 'シャインイチ')");
        jdbcTemplate.execute("INSERT INTO employee (id, department_id, name, name_kana) VALUES (2, 1, '社員二', 'シャインニ')");
        jdbcTemplate
                .execute("INSERT INTO employee (id, department_id, name, name_kana) VALUES (3, 1, '社員三', 'シャインサン')");
        jdbcTemplate
                .execute("INSERT INTO employee (id, department_id, name, name_kana) VALUES (4, 1, '渡辺太郎', 'ワタナベタロウ')");
        jdbcTemplate.execute("INSERT INTO employee (id, department_id, name, name_kana) VALUES (5, 1, '社員五', 'シャインゴ')");
        jdbcTemplate
                .execute("INSERT INTO employee (id, department_id, name, name_kana) VALUES (6, 1, '社員六', 'シャインロク')");

        // 事前条件：アカウントを3人登録 (ID1〜3は登録済み状態にする)
        jdbcTemplate.execute(
                "INSERT INTO employee_account (id, employee_id, name, password) VALUES (1, 1, 'user1', 'pass')");
        jdbcTemplate.execute(
                "INSERT INTO employee_account (id, employee_id, name, password) VALUES (2, 2, 'user2', 'pass')");
        jdbcTemplate.execute(
                "INSERT INTO employee_account (id, employee_id, name, password) VALUES (3, 3, 'user3', 'pass')");
    }

    /**
     * 項番1: findUnregisteredEmployees の正常系テスト
     */
    @Test
    void testFindUnregisteredEmployees_Success() {
        List<Employee> result = employeeRepository.findUnregisteredEmployees();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(4, result.get(0).getId());
        assertEquals("渡辺太郎", result.get(0).getName());
    }

    /**
     * 項番2: findNameById の正常系テスト
     */
    @Test
    void testFindNameById_Success() {
        String employeeName = employeeRepository.findNameById(4);
        assertEquals("渡辺太郎", employeeName);
    }
}
