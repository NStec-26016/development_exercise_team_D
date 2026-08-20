package com.example.fullness.stationary.service;

import com.example.fullness.stationary.entity.Employee;
import com.example.fullness.stationary.repository.EmployeeRepository;
import com.example.fullness.stationary.form.AccountRegisterForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AccountRegisterService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 未登録の社員一覧を取得（MyBatisのXML経由で安全に取得）
     */
    public List<Employee> getUnregisteredEmployees() {
        return employeeRepository.findUnregisteredEmployees();
    }

    /**
     * 社員IDから社員名を取得（実際の小文字テーブル名「employee」に修正）
     */
    public String getEmployeeNameById(String employeeId) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            return "";
        }
        try {
            // 💡 修正箇所：\"Employee\" から、実態である全小文字の employee に修正しました
            String sql = "SELECT name FROM employee WHERE id = ?";
            int id = Integer.parseInt(employeeId.trim());
            return jdbcTemplate.queryForObject(sql, String.class, id);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * アカウント名の重複チェック
     */
    public boolean isAccountNameDuplicate(String accountName) {
        if (accountName == null || accountName.trim().isEmpty())
            return false;

        String sql = "SELECT COUNT(*) FROM employee_account WHERE name = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, accountName);
        return count != null && count > 0;
    }

    /**
     * 新規アカウント登録処理
     */
    @Transactional
    public void register(AccountRegisterForm form) {
        String sql = "INSERT INTO employee_account (employee_id, name, password) VALUES (?, ?, ?)";

        int selectedEmployeeId = Integer.parseInt(form.getEmployeeId().trim());
        String hashedPassword = passwordEncoder.encode(form.getPassword());

        jdbcTemplate.update(sql, selectedEmployeeId, form.getAccountName(), hashedPassword);
    }
}
