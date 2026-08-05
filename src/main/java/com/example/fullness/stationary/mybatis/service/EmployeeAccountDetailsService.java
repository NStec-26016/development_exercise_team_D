package com.example.fullness.stationary.mybatis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.fullness.stationary.mybatis.entity.Employee;
import com.example.fullness.stationary.mybatis.entity.EmployeeAccount;
import com.example.fullness.stationary.mybatis.repository.EmployeeAccountRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 従業員アカウントの認証処理を行うための {@link UserDetailsService} 実装クラス。
 * <p>
 * Spring Security と連携し、データベース（MyBatis）から取得したアカウント情報を基に
 * ユーザー認証および認可に必要な {@link UserDetails} オブジェクトを構築します。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 */
@Service
@Transactional(readOnly = true)
public class EmployeeAccountDetailsService implements UserDetailsService {

    /** 従業員アカウントのデータアクセスを行うリポジトリ */
    @Autowired
    EmployeeAccountRepository employeeAcountRepository;
    @Autowired
    EmployeeAccountDetails employeeAccountDetails;

    /**
     * ユーザー名（アカウント名）をキーにデータベースを検索し、ユーザー詳細情報を取得します。
     * 
     * @param username 認証を試みるユーザーの名前
     * @return 認証されたユーザーの情報を持つ {@link UserDetails} オブジェクト
     * @throws UsernameNotFoundException 指定されたユーザー名に該当するアカウントがデータベースに存在しない場合
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        EmployeeAccount employeeAccount = employeeAcountRepository.findByName(username);
        if (employeeAccount == null) {
            throw new UsernameNotFoundException("user not found.");
        }
        Collection<GrantedAuthority> authorites = (Collection<GrantedAuthority>) employeeAccountDetails
                .getAuthorities();
        return new EmployeeAccountDetails(employeeAccount, authorites);
    }

}