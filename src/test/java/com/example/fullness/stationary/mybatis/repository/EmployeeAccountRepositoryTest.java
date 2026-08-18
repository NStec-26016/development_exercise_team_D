package com.example.fullness.stationary.mybatis.repository;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.fullness.stationary.mybatis.entity.EmployeeAccount;

@MybatisTest(properties = "mybatis.type-aliases-package=com.example.fullness.stationary.mybatis.entity", excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})

public class EmployeeAccountRepositoryTest {

    @Autowired
    private EmployeeAccountRepository repository;

    // 存在するアカウント名での検索
    @Test
    @Sql("/com/example/fullness/stationary/mybatis/repository/EmployeeAccountRepository_findByName.sql")
    void testFindByName_OK() {
        // 引数（入力値）に「丸ちゃん」を指定して実行
        EmployeeAccount account = repository.findByName("丸ちゃん");

        // 仕様書の期待結果と1対1で一致する検証コード
        assertThat(account).isNotNull();
        assertThat(account.getId()).isEqualTo(1);
        assertThat(account.getEmployeeId()).isEqualTo(1);
        assertThat(account.getName()).isEqualTo("丸ちゃん");
        assertThat(account.getPassword()).isEqualTo("$2a$10$wO3l2UiwZ3U13B0r8G9T2O6ZfL3r2zWjR3M7q6Nn/y5u8u7xMvKy6");
    }

    // 引数にnullを渡した場合の検索
    @Test
    @Sql("/com/example/fullness/stationary/mybatis/repository/EmployeeAccountRepository_findByName.sql")
    void testFindByName_NG1() {
        // 引数に存在しないアカウント名を指定して実行
        EmployeeAccount account = repository.findByName("unknown");

        // 「account == null」を検証
        assertThat(account).isNull();
    }

    // 引数にnullを渡した場合の検索
    @Test
    @Sql("/com/example/fullness/stationary/mybatis/repository/EmployeeAccountRepository_findByName.sql")
    void testFindByName_NG2() {
        // 引数にnullを指定して実行
        EmployeeAccount account = repository.findByName(null);

        // 「account == null」を検証
        assertThat(account).isNull();
    }

    // ログイン失敗回数とロック状態の更新テスト（追加分）
    @Test
    @Sql("/com/example/fullness/stationary/mybatis/repository/EmployeeAccountRepository_findByName.sql")
    void updateLockStatus_OK() {
        // 1. まず元のデータを1件取得する（丸ちゃん）
        EmployeeAccount account = repository.findByName("丸ちゃん");
        assertThat(account).isNotNull();

        // 2. 失敗回数やロック状態を変更する（※Entityに項目がある想定、適宜変更してください）
        // account.setFailedAttempts(3);
        // account.setIsLocked(true);

        // 3. 更新メソッドを実行
        repository.updateLockStatus(account);

        // 4. 再度DBから取得して、値が本当に書き換わっているか（Expect）を検証する
        EmployeeAccount updatedAccount = repository.findByName("丸ちゃん");
        assertThat(updatedAccount).isNotNull();
        // assertThat(updatedAccount.getFailedAttempts()).isEqualTo(3);
        // assertThat(updatedAccount.getIsLocked()).isTrue();
    }
    
}
