package com.example.fullness.stationary.service;

import com.example.fullness.stationary.entity.Employee;
import com.example.fullness.stationary.repository.EmployeeRepository;
import com.example.fullness.stationary.repository.EmployeeAccountRepository; // ★追加
import com.example.fullness.stationary.form.AccountRegisterForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AccountRegisterService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeAccountRepository employeeAccountRepository; // ★JdbcTemplateから変更

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 未登録の社員一覧を取得
     */
    public List<Employee> getUnregisteredEmployees() {
        return employeeRepository.findUnregisteredEmployees();
    }

    /**
     * 社員IDから社員名を取得
     */
    public String getEmployeeNameById(String employeeId) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            return "";
        }

        int id = Integer.parseInt(employeeId.trim());
        return employeeRepository.findNameById(id);
    }

    /**
     * アカウント名の重複チェック
     */
    public boolean isAccountNameDuplicate(String accountName) {
        if (accountName == null || accountName.trim().isEmpty()) {
            return false;
        }
        // 既存リポジトリの countByName を呼び出す
        return employeeAccountRepository.countByName(accountName) > 0;
    }

    /**
     * 新規アカウント登録処理
     */
    @Transactional
    public void register(AccountRegisterForm form) {
        int selectedEmployeeId = Integer.parseInt(form.getEmployeeId().trim());
        String hashedPassword = passwordEncoder.encode(form.getPassword());

        com.example.fullness.stationary.entity.EmployeeAccount account = new com.example.fullness.stationary.entity.EmployeeAccount();
        account.setEmployeeId(selectedEmployeeId);
        account.setName(form.getAccountName());
        account.setPassword(hashedPassword);

        employeeAccountRepository.insertEmployeeAccount(account);
    }
}
