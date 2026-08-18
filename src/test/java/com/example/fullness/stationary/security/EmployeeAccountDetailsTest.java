package com.example.fullness.stationary.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;

import com.example.fullness.stationary.entity.EmployeeAccount;
import com.example.fullness.stationary.security.EmployeeAccountDetails;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link EmployeeAccountDetails} の挙動を検証するための単体テストクラスです。
 * 
 * データベースから取得したエンティティの情報が、Spring Securityの認証用オブジェクト（UserDetails）として
 * 正しくマッピングされるか、「10分間のアカウントロック判定」が時間経過に沿って
 * 狙い通りに動くかを厳密に検証します。
 * 
 * @author 丸本
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
public class EmployeeAccountDetailsTest {

    /**
     * アカウントに紐付ける権限リストのモック（身代わり）です。
     * 今回の判定ロジック自体には影響しないため、空のリストを渡すために用意します。
     */
    @Mock
    private Collection<GrantedAuthority> authorities;

    /**
     * エンティティから取得した基本情報（ユーザー名やパスワード）が、
     * Spring Securityのメソッド経由で正しくそのまま返却されるかを検証します。
     */
    @Test
    @DisplayName("エンティティに設定されたユーザー名、パスワード、権限が正しく取得できること")
    void GettersAndBasicProperties_Test() {
        // 【1. 準備 】検証用のテストデータをセットした本物のエンティティを用意
        EmployeeAccount account = new EmployeeAccount();
        account.setName("丸ちゃん");
        account.setPassword("encodedPassword123");

        // テスト対象クラスをインスタンス化（コンスタラクタにデータを渡す）
        EmployeeAccountDetails details = new EmployeeAccountDetails(account, authorities);

        // 【2. 実行 ＆ 3. 検証 】
        // メソッドがエンティティに格納した通りの値を返すか答え合わせ
        assertEquals("丸ちゃん", details.getUsername());
        assertEquals("encodedPassword123", details.getPassword());
        assertEquals(authorities, details.getAuthorities());
    }

    /**
     * ロック日時（lockTime）が何も設定されていない（null）通常アカウントの場合、
     * ロックされていない状態（true）と判定されるかを検証します。
     */
    @Test
    @DisplayName("ロック日時が設定されていない場合、isAccountNonLockedがtrueを返すこと")
    void IsAccountNonLocked_WhenLockTimeIsNull_Test() {
        // 【1. 準備 】ロック日時が null（初期状態）のアカウントを用意
        EmployeeAccount account = new EmployeeAccount();
        account.setLockTime(null);

        EmployeeAccountDetails details = new EmployeeAccountDetails(account, authorities);

        // 【2. 実行 】ロックチェックメソッドを動かす
        boolean isNonLocked = details.isAccountNonLocked();

        // 【3. 検証 】ロックされていないので「true」になることを確認
        assertTrue(isNonLocked);
    }

    /**
     * アカウントにロック日時がセットされてから、まだ10分が経過していない場合、
     * 正しく「ロック中（false）」と判定されるかを検証します。
     */
    @Test
    @DisplayName("ロックされてから10分未満の場合、isAccountNonLockedがfalseを返すこと")
    void IsAccountNonLocked_WhenWithinTenMinutes_Test() {
        // 【1. 準備 】たった今（5分前など）ロックされたアカウントを再現
        // 現在時刻から5分前をロック時刻として設定します
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);

        EmployeeAccount account = new EmployeeAccount();
        account.setLockTime(fiveMinutesAgo);

        EmployeeAccountDetails details = new EmployeeAccountDetails(account, authorities);

        // 【2. 実行 】ロックチェックメソッドを動かす
        boolean isNonLocked = details.isAccountNonLocked();

        // 【3. 検証 】まだ10分経っていないので、ロック中という意味の「false」になることを確認
        assertFalse(isNonLocked);
    }

    /**
     * 【重要ケース：自動ロック解除】
     * アカウントにロック日時がセットされてから、すでに10分以上が経過している場合、
     * 自動的に「ロック解除（true）」と判定されるかを検証します。
     */
    @Test
    @DisplayName("ロックされてから10分以上経過している場合、isAccountNonLockedがtrueを返すこと")
    void IsAccountNonLocked_WhenOverTenMinutes_Test() {
        // 【1. 準備 】11分前にロックされ、すでにペナルティ時間を終えたアカウントを再現
        // 現在時刻から11分前をロック時刻として設定します
        LocalDateTime elevenMinutesAgo = LocalDateTime.now().minusMinutes(11);

        EmployeeAccount account = new EmployeeAccount();
        account.setLockTime(elevenMinutesAgo);

        EmployeeAccountDetails details = new EmployeeAccountDetails(account, authorities);

        // 【2. 実行 】ロックチェックメソッドを動かす
        boolean isNonLocked = details.isAccountNonLocked();

        // 【3. 検証 】10分を過ぎたので、ロックは解除されているという意味の「true」になることを確認
        assertTrue(isNonLocked);
    }

    /**
     * 有効期限やアカウントの有効フラグなど、今回は使用せず
     * 常に固定で true を返すように設計されている仕様通りの挙動を検証します。
     */
    @Test
    @DisplayName("有効期限や有効フラグのメソッドが、常にtrueを返すこと")
    void AlwaysTrueMethods_Test() {
        // 【1. 準備 】中身が空のアカウントを用意
        EmployeeAccount account = new EmployeeAccount();
        EmployeeAccountDetails details = new EmployeeAccountDetails(account, authorities);

        // 【2. 実行 ＆ 3. 検証 】
        // 実装仕様通り、すべて true が返ってくるか答え合わせ
        assertTrue(details.isAccountNonExpired());
        assertTrue(details.isCredentialsNonExpired());
        assertTrue(details.isEnabled());
    }
}
