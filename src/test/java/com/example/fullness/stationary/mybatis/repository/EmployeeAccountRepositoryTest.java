package com.example.fullness.stationary.mybatis.repository;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.fullness.stationary.mybatis.entity.EmployeeAccount;

@MybatisTest
public class EmployeeAccountRepositoryTest {

    @Autowired
    private EmployeeAccountRepository repository;

    // 存在するアカウント名での検索
    @Test
    @Sql("/test-data/EmployeeAccountRepository_findByName.sql")
    void EmployeeAccountRepositoryTest_OK() {
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
    @Sql("/test-data/EmployeeAccountRepository_findByName.sql")
    void EmployeeAccountRepositoryTest_NG1() {
        // 引数に存在しないアカウント名を指定して実行
        EmployeeAccount account = repository.findByName("unknown");

        // 「account == null」を検証
        assertThat(account).isNull();
    }

    // 引数にnullを渡した場合の検索
    @Test
    @Sql("/test-data/EmployeeAccountRepository_findByName.sql")
    void EmployeeAccountRepositoryTest_NG2() {
        // 引数にnullを指定して実行
        EmployeeAccount account = repository.findByName(null);

        // 「account == null」を検証
        assertThat(account).isNull();
    }
}
