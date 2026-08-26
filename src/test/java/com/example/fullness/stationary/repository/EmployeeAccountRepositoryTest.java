package com.example.fullness.stationary.repository;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;

import com.example.fullness.stationary.entity.EmployeeAccount;
import com.example.fullness.stationary.repository.EmployeeAccountRepository;

import static org.assertj.core.api.Assertions.assertThat;

// @MybatisTest(properties = "mybatis.type-aliases-package=com.example.fullness.stationary.mybatis.entity")
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EmployeeAccountRepositoryTest {

    @Autowired
    private EmployeeAccountRepository repository;

    // 存在するアカウント名での検索
    @Test
    @Sql("/com/example/fullness/stationary/repository/EmployeeAccountRepository_findByName.sql")
    void EmployeeAccountRepositoryTest_OK() {
        // 引数（入力値）に「marumoto」を指定して実行
        EmployeeAccount account = repository.findByName("marumoto");

        // 仕様書の期待結果と1対1で一致する検証コード
        assertThat(account).isNotNull();
        assertThat(account.getId()).isEqualTo(1);
        assertThat(account.getEmployeeId()).isEqualTo(1);
        assertThat(account.getName()).isEqualTo("marumoto");
        assertThat(account.getPassword()).isEqualTo("maru1");
    }

    // 引数にnullを渡した場合の検索
    @Test
    @Sql("/com/example/fullness/stationary/repository/EmployeeAccountRepository_findByName.sql")
    void EmployeeAccountRepositoryTest_NG1() {
        // 引数に存在しないアカウント名を指定して実行
        EmployeeAccount account = repository.findByName("unknown");

        // 「account == null」を検証
        assertThat(account).isNull();
    }

    // 引数にnullを渡した場合の検索
    @Test
    @Sql("/com/example/fullness/stationary/repository/EmployeeAccountRepository_findByName.sql")
    void EmployeeAccountRepositoryTest_NG2() {
        // 引数にnullを指定して実行
        EmployeeAccount account = repository.findByName(null);

        // 「account == null」を検証
        assertThat(account).isNull();
    }

    // 【UC09 アカウント登録】1. 重複していないアカウント名でのカウント数取得 (期待値: 0)
    @Test
    @Sql("/com/example/fullness/stationary/repository/EmployeeAccountRepository_insertAndCount.sql")
    void EmployeeAccountRepositoryTest_countByName_OK() {
        // まだ誰も使っていないアカウント名「watanabe4」を指定して実行
        int count = repository.countByName("watanabe4");

        // 重複なし（0件）であることを検証
        assertThat(count).isEqualTo(0);
    }

    // 【UC09 アカウント登録】2. 新規アカウントの登録処理 (期待値: 登録成功)
    @Test
    @Sql("/com/example/fullness/stationary/repository/EmployeeAccountRepository_insertAndCount.sql")
    void EmployeeAccountRepositoryTest_insertEmployeeAccount_OK() {
        // テスト用データ（Entity）の準備
        EmployeeAccount account = new EmployeeAccount();
        account.setEmployeeId(4); // 事前SQLで用意した渡辺太郎のID
        account.setName("watanabe4");
        account.setPassword("hashedPasswordXYZ");

        // 登録処理（INSERT）を実行
        int rows = repository.insertEmployeeAccount(account);

        // 1行正常に挿入（影響を受けた行数が1）されたことを検証
        assertThat(rows).isEqualTo(1);
    }
}
