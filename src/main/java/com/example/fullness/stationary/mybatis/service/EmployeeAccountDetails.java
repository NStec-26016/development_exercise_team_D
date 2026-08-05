package com.example.fullness.stationary.mybatis.service;

import com.example.fullness.stationary.mybatis.entity.EmployeeAccount;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

/**
 * Spring Security で利用する従業員アカウントの認証・認可詳細情報を保持するクラス。
 * <p>
 * データベースから取得した {@link EmployeeAccount} エンティティをラップし、
 * Spring Security が認証処理を行うために必要なユーザー情報を提供します。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 */
public class EmployeeAccountDetails implements UserDetails {

    /** 従業員アカウントのエンティティ情報 */
    private final EmployeeAccount employeeAccount;

    /** ユーザーに付与された権限（ロール等）のコレクション */
    private final Collection<GrantedAuthority> authorites;

    /**
     * 指定された従業員アカウントと権限情報を使用して、新しいオブジェクトを構築します。
     * 
     * @param employeeAccount 従業員アカウントのエンティティ
     * @param authorites ユーザーに付与する権限情報のコレクション
     */
    public EmployeeAccountDetails(EmployeeAccount employeeAccount, Collection<GrantedAuthority> authorites){
        this.employeeAccount = employeeAccount;
        this.authorites = authorites;
    }

    /**
     * ユーザーに付与された権限（ロール等）を返します。
     * 
     * @return 権限オブジェクトのコレクション
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorites;
    }

    /**
     * ユーザーの認証に使用するパスワードを返します。
     * 
     * @return 暗号化されたパスワード文字列
     */
    @Override
    public String getPassword() {
        return employeeAccount.getPassword();
    }

    /**
     * ユーザーの認証に使用するユーザー名（アカウント名）を返します。
     * 
     * @return ユーザー名
     */
    @Override
    public String getUsername() {
        return employeeAccount.getName();
    }

    /**
     * アカウントの有効期限が切れていないかどうかを判定します。
     * 
     * @return 常に {@code true} (有効)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * アカウントがロックされていないかどうかを判定します。
     * 
     * @return 常に {@code true} (アンロック状態)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 認証資格情報（パスワード）の有効期限が切れていないかどうかを判定します。
     * 
     * @return 常に {@code true} (有効)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
