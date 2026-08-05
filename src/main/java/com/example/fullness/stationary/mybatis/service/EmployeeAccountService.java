package com.example.fullness.stationary.mybatis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.fullness.stationary.mybatis.entity.EmployeeAccount;
import com.example.fullness.stationary.mybatis.repository.EmployeeAccountRepository;

@Service
public class EmployeeAccountService {

    @Autowired
    private EmployeeAccountRepository employeeAccountRepository; 

    @Autowired
    private PasswordEncoder passwordEncoder;  

    @Transactional 
    public void create(EmployeeAccount employeeAccount) { 
        // 1. 従業員アカウントから生のパスワードを取得
        String password = employeeAccount.getPassword(); 
        
        // 2. パスワードをハッシュ化して、従業員アカウントにセットし直す
        employeeAccount.setPassword(passwordEncoder.encode(password)); 
        
        // 3. リポジトリを呼び出してデータベースに保存
        employeeAccountRepository.insertEmployeeAccount(Employee employeeAccount); 
    } 
}