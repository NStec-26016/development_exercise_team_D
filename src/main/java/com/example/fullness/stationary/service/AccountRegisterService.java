package com.example.fullness.stationary.service;

import com.example.fullness.stationary.entity.Employee;
import com.example.fullness.stationary.entity.EmployeeAccount;
import com.example.fullness.stationary.repository.EmployeeRepository;
import com.example.fullness.stationary.repository.EmployeeAccountRepository;
import com.example.fullness.stationary.form.AccountRegisterForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class AccountRegisterService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeAccountRepository employeeAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * アカウント未登録の社員一覧を取得
     */
    public List<Employee> getUnregisteredEmployees() {
        return employeeRepository.findUnregisteredEmployees();
    }

    /**
     * 社員IDから社員名を取得（文字列・数値の型不一致を安全に吸収）
     */
    public String getEmployeeNameById(Object idObj) {
        if (idObj == null) {
            return "";
        }
        try {
            Integer id = (idObj instanceof Integer) ? (Integer) idObj : Integer.valueOf(idObj.toString().trim());
            return employeeRepository.findById(id).map(Employee::getName).orElse("");
        } catch (NumberFormatException e) {
            return "";
        }
    }

    /**
     * アカウント名の重複チェック
     */
    public boolean isAccountNameDuplicate(String accountName) {
        if (accountName == null || accountName.trim().isEmpty()) {
            return false;
        }
        return employeeAccountRepository.countByName(accountName) > 0;
    }

    /**
     * 登録完了したアカウント情報をDBから再取得（完了画面用データ）
     */
    public EmployeeAccount getRegisteredAccountByName(String name) {
        return employeeAccountRepository.findByName(name);
    }

    /**
     * 新規担当者アカウントのDB登録処理
     */
    @Transactional
    public void register(AccountRegisterForm form) {
        EmployeeAccount account = new EmployeeAccount();

        // フォームのString型IDをInteger型へ変換してセット
        if (form.getEmployeeId() != null && !form.getEmployeeId().trim().isEmpty()) {
            account.setEmployeeId(Integer.valueOf(form.getEmployeeId().trim()));
        }

        account.setName(form.getAccountName());

        // SecurityConfigのエンコーダー（現在は平文、将来ハッシュ化へ変更されてもそのまま連動）
        account.setPassword(passwordEncoder.encode(form.getPassword()));

        // ログイン制御用初期ステータスの設定（ロールはデフォルト値を想定）
        account.setEmployeeAccountRole("ROLE_USER");
        account.setFailedAttempts(0);
        account.setLockTime(null);

        employeeAccountRepository.insertEmployeeAccount(account);
    }
}
