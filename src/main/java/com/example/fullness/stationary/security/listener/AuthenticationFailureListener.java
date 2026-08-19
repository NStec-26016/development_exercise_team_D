package com.example.fullness.stationary.security.listener;

import com.example.fullness.stationary.entity.EmployeeAccount;
import com.example.fullness.stationary.repository.EmployeeAccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/**
 * ログイン失敗（パスワード間違いなど）を検知して、
 * 失敗回数のカウントアップやアカウントロックを行うクラスです。
 * 
 * @author 丸本
 * @version 1.0
 */
@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Autowired
    private EmployeeAccountRepository employeeAccountRepository;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        // ログイン画面で入力された「名前」を取得します
        String name = event.getAuthentication().getName();

        // データベースから該当の従業員データを取得します
        EmployeeAccount employeeAccount = employeeAccountRepository.findByName(name);

        // ユーザーが存在する場合のみ処理を行います
        if (employeeAccount != null) {
            // 現在の失敗回数を1増やします
            int newAttempts = employeeAccount.getFailedAttempts() + 1;
            employeeAccount.setFailedAttempts(newAttempts);

            // 失敗回数が5回に達したら、現在時刻をロック日時として設定します
            if (newAttempts >= 5) {
                employeeAccount.setLockTime(LocalDateTime.now());
            }

            // データベースの failedAttempts と lockTime を更新するSQLを呼び出します
            employeeAccountRepository.updateLockStatus(employeeAccount);

        }
    }
}
