package com.example.fullness.stationary.mybatis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.fullness.stationary.mybatis.entity.EmployeeAccount;
import com.example.fullness.stationary.mybatis.repository.EmployeeAccountRepository;

/**
 * 従業員アカウントの業務ロジックを管理するサービス実装クラス。
 * <p>
 * アカウント情報の新規登録時に、セキュリティ向上のためのパスワードハッシュ化や
 * トランザクション管理を伴うデータベースへの保存処理を提供します。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 */
@Service
public class EmployeeAccountService {

    /** 従業員アカウントのデータアクセスを行うリポジトリ */
    @Autowired
    private EmployeeAccountRepository employeeAccountRepository;

    /** パスワードを安全に暗号化（ハッシュ化）するためのエンコーダー */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 新しい従業員アカウントをデータベースに登録します。
     * <p>
     * 画面から入力された生のパスワードを {@link PasswordEncoder} を用いてハッシュ化し、
     * 安全な状態に上書きした上でリポジトリを経由してデータベースへ保存します。
     * このメソッドはトランザクション管理下で実行されます。
     * </p>
     * 
     * @param employeeAccount 登録する従業員アカウントのエンティティ情報（生のパスワードを含む）
     */
    @Transactional
    public void create(EmployeeAccount employeeAccount) {
        // 1. 従業員アカウントから生のパスワードを取得
        String password = employeeAccount.getPassword();

        // 2. パスワードをハッシュ化して、従業員アカウントにセットし直す
        employeeAccount.setPassword(passwordEncoder.encode(password));

        // 3. リポジトリを呼び出してデータベースに保存
        // employeeAccountRepository.insertEmployeeAccount(employeeAccount);
    }
}
