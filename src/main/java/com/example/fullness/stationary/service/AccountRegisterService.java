package com.example.fullness.stationary.service;

import com.example.fullness.stationary.entity.Employee;
import com.example.fullness.stationary.entity.EmployeeAccount;
import com.example.fullness.stationary.form.AccountRegisterForm;
import com.example.fullness.stationary.repository.EmployeeAccountRepository;
import com.example.fullness.stationary.repository.EmployeeRepository;
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
    private EmployeeAccountRepository employeeAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * アカウントが未登録の社員リストをDBから取得
     */
    public List<Employee> getUnregisteredEmployees() {
        return employeeRepository.findUnregisteredEmployees();
    }

    /**
     * 社員IDから社員名を取得
     */
    public String getEmployeeNameById(String id) {
        if (id == null || id.isEmpty()) {
            return "未選択";
        }
        Employee employee = employeeRepository.findById(Integer.valueOf(id));
        return (employee != null) ? employee.getName() : "未選択";
    }

    /**
     * アカウント名の重複チェック
     */
    public boolean isAccountNameDuplicate(String accountName) {
        // ログイン側のRepositoryに追記した countByName を呼び出す
        int count = employeeAccountRepository.countByName(accountName);
        return count > 0;
    }

    /**
     * アカウントの新規登録処理
     */
    @Transactional
    public void register(AccountRegisterForm form) {
        // SecurityConfigの設定に連動（現在はNoOpのため生のまま、将来暗号化がONになれば自動で追従）
        String processedPassword = passwordEncoder.encode(form.getPassword());

        EmployeeAccount account = new EmployeeAccount();
        
        // ログインシステム側のEntity定義（型・変数名）に完全適合
        account.setEmployeeId(Integer.valueOf(form.getEmployeeId()));
        account.setName(form.getAccountName());
        account.setPassword(processedPassword);
        
        // 演習ルールに合わせた初期値設定
        account.setEmployeeAccountRole("ROLE_USER"); 
        account.setFailedAttempts(0);
        account.setLockTime(null);

        // ★ログイン側が事前に用意してくれていた本物の登録メソッドを呼び出す
        employeeAccountRepository.insertEmployeeAccount(account);
    }
}
