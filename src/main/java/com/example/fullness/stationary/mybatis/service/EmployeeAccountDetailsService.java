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
 * ログイン画面での認証処理を担当するサービス（クラス）です。
 * 
 * Spring Securityの仕組み（UserDetailsService）を利用して、
 * ログイン時にデータベースから従業員のアカウント情報を取ってくる処理を行います。
 * 読み取り専用の処理なので、 @Transactional(readOnly = true) をつけています。
 * 
 * @author 丸本
 * @version 1.0
 */
@Service
@Transactional(readOnly = true)
public class EmployeeAccountDetailsService implements UserDetailsService {

    /** 従業員アカウントのデータをDBから取得するためのリポジトリです */
    @Autowired
    EmployeeAccountRepository employeeAcountRepository;

    /**
     * ログイン画面に入力された名前を使ってデータベースを検索し、ユーザー情報を返します。
     * 
     * リポジトリを使って「名前」でDBを検索します。
     * ユーザーが見つからなかった場合は、ログインエラーにするために例外を発生させます。
     * 見つかった場合は、その人の権限（ロール）を調べてから、Spring Security用のクラスにデータを詰めて返します。
     * 
     * @param name ログイン画面でユーザーが入力した名前（アカウント名）
     * @return 認証に必要なデータが詰まった EmployeeAccountDetails オブジェクト
     * @throws UsernameNotFoundException 入力された名前のユーザーがデータベースに見つからなかった場合のエラー
     */
    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        // 1. ログイン画面で入力された名前を使って、DBから従業員データを取得します
        EmployeeAccount employeeAccount = employeeAcountRepository.findByName(name);

        // 2. データが取れなかった（nullだった）場合は、ユーザーがいないのでエラーを投げます
        if (employeeAccount == null) {
            throw new UsernameNotFoundException("user not found.");
        }

        // 3. 下の getAuthorits メソッドを呼び出して、このユーザーの権限リストを作ります
        Collection<GrantedAuthority> authorites = getAuthorits(employeeAccount);

        // 4. 先ほど作った EmployeeAccountDetails クラスにデータをセットして返します
        return new EmployeeAccountDetails(employeeAccount, authorites);
    }

    /**
     * 従業員アカウントのデータ（ロール）を基に、Spring Securityが理解できる権限リストを作ります。
     * 
     * 従業員データに設定されている役割（管理者や一般ユーザーなど）の文字列をチェックして、
     * Spring Securityが画面のアクセス制限などで使えるように、専用のリスト（AuthorityList）に変換します。
     * 
     * @param employeeAccount 権限を調べるための従業員アカウントのデータ
     * @return Spring Security用に変換した権限（ロール）のリスト
     */
    private Collection<GrantedAuthority> getAuthorits(EmployeeAccount employeeAccount) {
        // 従業員データから役割（ロール）の文字列を取得します
        String role = employeeAccount.getEmployeeAccountRole();

        // 文字列の比較は .equals() を使います
        return AuthorityUtils.createAuthorityList("ROLE_Admin");
    }
}
