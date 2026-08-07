package com.example.fullness.stationary.mybatis.service;

import com.example.fullness.stationary.mybatis.entity.EmployeeAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class EmployeeAccountDetailsTest {

    private EmployeeAccount employeeAccountMock;
    private Collection<GrantedAuthority> authorities;
    private EmployeeAccountDetails target;

    @BeforeEach
    void setUp() {
        // 1. テストごとに依存オブジェクトのモックとダミーデータを初期化
        // Mockito.mock(...) を使って、中身が空っぽの 「従業員データの身代わり（モック）」 を作成し、箱に入れてる
        employeeAccountMock = Mockito.mock(EmployeeAccount.class);
        authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        // 2. テスト対象のクラスにモックを注入してインスタンス化
        // 「一般ユーザー（ROLE_USER）」というダミーの権限を1つ持ったリストを作成し、箱に入れています。
        target = new EmployeeAccountDetails(employeeAccountMock, authorities);
    }

    @Test
    @DisplayName("getAuthorities()がコンストラクタで渡した権限リストを正しく返却すること")
    void GetAuthorities_Test_1() {
        // 実行 & 検証
        Collection<? extends GrantedAuthority> actual = target.getAuthorities();
        assertEquals(authorities, actual);
    }

    @Test
    @DisplayName("getAuthorities()がコンストラクタで渡した権限と同じ数の権限が返ってくること")
    void GetAuthorities_Test_2() {
        // 実行 & 検証
        Collection<? extends GrantedAuthority> actual = target.getAuthorities();
        assertEquals(1, actual.size());
    }

    @Test
    @DisplayName("getAuthorities()がコンストラクタで渡した権限が正しく返却すること")
    void GetAuthorities_Test_3() {
        // 実行 & 検証
        Collection<? extends GrantedAuthority> actual = target.getAuthorities();
        assertTrue(actual.contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    @DisplayName("getPassword()がEmployeeAccountのパスワードを正しく返却すること")
    void GetPassword_Test() {
        // モックの振る舞いを設定（暗号化済みのダミーパスワードを返すようにする）
        String expectedPassword = "$2a$10$xyz123mockedPasswordEncrypted...";
        when(employeeAccountMock.getPassword()).thenReturn(expectedPassword);

        // 実行 & 検証
        assertEquals(expectedPassword, target.getPassword());
    }

    @Test
    @DisplayName("getUsername()がEmployeeAccountの名前を正しく返却すること")
    void GetUsername_Test() {
        // モックの振る舞いを設定（従業員名を返すようにする）
        String expectedUsername = "丸本 翔太郎";
        when(employeeAccountMock.getName()).thenReturn(expectedUsername);

        // 実行 & 検証
        assertEquals(expectedUsername, target.getUsername());
    }

    @Test
    @DisplayName("アカウント有効期限チェック")
    void isAccountNonExpired_Test_1() {
        // 固定で true を返す仕様になっているか検証
        assertTrue(target.isAccountNonExpired(), "アカウント有効期限チェック");

    }

    @Test
    @DisplayName("アカウントロックチェック")
    void isAccountNonLocked_Test() {
        // 固定で true を返す仕様になっているか検証
        assertTrue(target.isAccountNonLocked(), "アカウントロックチェック");
    }

    @Test
    @DisplayName("パスワード有効期限チェック")
    void isCredentialsNonExpired_Test() {
        // 固定で true を返す仕様になっているか検証
        assertTrue(target.isCredentialsNonExpired(), "パスワード有効期限チェック");
    }

    @Test
    @DisplayName("アカウント有効フラグチェック")
    void isEnabled_Test() {
        // 固定で true を返す仕様になっているか検証
        assertTrue(target.isEnabled(), "アカウント有効フラグチェック");
    }
}
