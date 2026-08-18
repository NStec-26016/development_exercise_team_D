package com.example.fullness.stationary.mybatis.service;

import com.example.fullness.stationary.mybatis.entity.EmployeeAccount;
import com.example.fullness.stationary.mybatis.repository.EmployeeAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * ログイン成功を検知して、
 * 過去の失敗回数のリセットやロック日時のクリアを行うクラスです。
 */
@Component
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private EmployeeAccountRepository employeeAccountRepository;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        // ログインに成功したユーザーの名前を取得します
        String name = event.getAuthentication().getName();

        // データベースから該当の従業員データを取得します
        EmployeeAccount employeeAccount = employeeAccountRepository.findByName(name);

        // ユーザーが存在し、かつ過去に失敗した履歴がある場合のみリセット処理を行います
        if (employeeAccount != null
                && (employeeAccount.getFailedAttempts() > 0 || employeeAccount.getLockTime() != null)) {

            // 失敗回数を0に戻し、ロック日時もクリア（null）にします
            employeeAccount.setFailedAttempts(0);
            employeeAccount.setLockTime(null);

            // データベースの failedAttempts と lockTime を更新します
            employeeAccountRepository.updateLockStatus(employeeAccount);
        }
    }
}
