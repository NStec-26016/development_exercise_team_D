package com.example.fullness.stationary.mybatis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.fullness.stationary.mybatis.entity.EmployeeAccount;
import com.example.fullness.stationary.mybatis.repository.EmployeeAccountRepository;
import java.util.Collection;

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

    /**
     * ユーザー名（アカウント名）をキーにデータベースを検索し、ユーザー詳細情報を取得します。
     * 
     * @param username 認証を試みるユーザーの名前
     * @return 認証されたユーザーの情報を持つ {@link UserDetails} オブジェクト
     * @throws UsernameNotFoundException 指定されたユーザー名に該当するアカウントがデータベースに存在しない場合
     */
    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        EmployeeAccount employeeAccount = employeeAcountRepository.findByName(name);
        if (employeeAccount == null) {
            throw new UsernameNotFoundException("user not found.");
        }
        Collection<GrantedAuthority> authorites = getAuthorits(employeeAccount); 
        return new EmployeeAccountDetails(employeeAccount, authorites); 
    } 

    /**
     * 従業員アカウントの役割（ロール）文字列を基に、Spring Security用の権限コレクションを生成します。
     * <p>
     * アカウントが保持するロール文字列を判定し、上位のロールには下位の権限（USERやGUESTなど）も含めて
     * 段階的に権限リスト（階層的な権限モデル）を構築します。
     * </p>
     * 
     * @param employeeAccount 権限を判定する従業員アカウント情報
     * @return 付与された {@link GrantedAuthority} オブジェクトのコレクション
     */
    private Collection<GrantedAuthority> getAuthorits(EmployeeAccount employeeAccount) { 
        // 文字列の取得メソッド名が getEmployeeAccountRole() の場合
        String role = employeeAccount.getEmployeeAccountRole();

        // 文字列の比較は .equals() を使います
            return AuthorityUtils.createAuthorityList("Admin");  
    }
}
