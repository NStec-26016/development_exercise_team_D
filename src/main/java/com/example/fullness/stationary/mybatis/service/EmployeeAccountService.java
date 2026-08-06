package com.example.fullness.stationary.mybatis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.fullness.stationary.mybatis.entity.EmployeeAccount;
import com.example.fullness.stationary.mybatis.repository.EmployeeAccountRepository;

/**
 * 従業員アカウントの新規登録処理を行うサービス（クラス）です。
 * 
 * ユーザー登録画面から送られてきたアカウント情報をデータベースに保存する処理を担当します。
 * セキュリティを高くするためにパスワードを暗号化（ハッシュ化）する処理や、
 * 「データの不整合を防ぐためのトランザクション管理（@Transactional）」を取り入れています。
 * 
 * @author マルモト
 * @version 1.0
 */
@Service
public class EmployeeAccountService {

    /** データベースへデータを保存するために、リポジトリクラスをインジェクション（DI）しています */
    @Autowired
    private EmployeeAccountRepository employeeAccountRepository;

    /** パスワードを安全に暗号化（ハッシュ化）するための暗号化エンジン（エンコーダー）です */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 新しい従業員アカウントをデータベースに登録（保存）するメソッドです。
     * 
     * 画面から入力された生のパスワードをそのまま保存すると危険なので、
     * PasswordEncoder を使って暗号化された文字に変換してから、リポジトリ経由でDBへ保存します。
     * 万が一、途中でエラーが起きてもデータが中途半端に残らないように、@Transactional をつけています。
     * 
     * @param employeeAccount 画面から入力されたデータが入っている従業員アカウントのエンティティ
     */
    @Transactional
    public void create(EmployeeAccount employeeAccount) {
        // 1. 従業員アカウントのデータから、画面に入力された生のパスワードを取り出します
        String password = employeeAccount.getPassword();

        // 2. 取り出したパスワードを暗号化（ハッシュ化）して、元のデータにセットし直します
        employeeAccount.setPassword(passwordEncoder.encode(password));

        // 3. リポジトリのメソッドを呼び出して、データベースに新しくデータを追加（インサート）します
        employeeAccountRepository.insertEmployeeAccount(employeeAccount);
    }
}
