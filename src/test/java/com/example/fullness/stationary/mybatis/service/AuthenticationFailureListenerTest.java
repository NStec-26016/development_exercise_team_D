package com.example.fullness.stationary.mybatis.service;

import com.example.fullness.stationary.mybatis.entity.EmployeeAccount;
import com.example.fullness.stationary.mybatis.repository.EmployeeAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * {@link AuthenticationFailureListener} の挙動を検証するための単体テストクラスです。
 * 
 * ログイン失敗イベント（パスワード間違いなど）が発生した際に、
 * 「失敗回数が正しく増えるか」「5回目で本当にロックがかかるか」といった
 * セキュリティ上の重要なロジックを、モック（身代わり）を使って高速に検証します。
 * 
 * @author 丸本
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
public class AuthenticationFailureListenerTest {

    /**
     * テスト対象となる本物のクラスです。
     * 内部にあるリポジトリに対し、自動的に {@code @Mock} が注入されます。
     */
    @InjectMocks
    private AuthenticationFailureListener listener;

    /**
     * データベース通信をシミュレートするためのリポジトリのモック（身代わり）です。
     */
    @Mock
    private EmployeeAccountRepository employeeAccountRepository;

    /**
     * Spring Securityが「パスワード間違い」を検知したときに発行するイベントのモックです。
     */
    @Mock
    private AuthenticationFailureBadCredentialsEvent event;

    /**
     * イベントの内部に格納されている、未認証ユーザー情報のモックです。
     */
    @Mock
    private Authentication authentication;

    /**
     * テストで使用する検証用のユーザー名です。
     */
    private final String testName = "丸ちゃん";

    /**
     * 各テストメソッド（{@code @Test}）が実行される直前に、必ず毎回呼び出される前処理メソッドです。
     * 
     * ログイン失敗イベントから入力されたユーザー名を取得するチェーン
     * {@code event.getAuthentication().getName()} が、すべてのテストケースで
     * 確実に {@code "丸ちゃん"} を返すようにモックの台本を設定します。
     */
    @BeforeEach
    void setUp() {
        // event.getAuthentication() が呼ばれたら、身代わりの authentication を返しなさい
        lenient().when(event.getAuthentication()).thenReturn(authentication);
        // authentication.getName() が呼ばれたら、文字列 "丸ちゃん" を返しなさい
        lenient().when(authentication.getName()).thenReturn(testName);
    }

    /**
     * 【テストケース1：カウントアップルート（ロックなし）】
     * 過去の失敗回数がまだ少なく（例: 2回）、今回の失敗で3回目になる場合の挙動を検証します。
     * 
     * ＜期待される結果＞
     * 1. 失敗回数が 2 から 3 へ「1つだけ」増えること
     * 2. まだ上限（5回）に達していないため、ロック日時（lockTime）は null（空）のままであること
     * 3. データベースの更新メソッド（updateLockStatus）が確実に1回呼び出されること
     */
    @Test
    @DisplayName("ログイン失敗時、現在の失敗回数が1回カウントアップされること（まだ5回未満ならロックされない）")
    void OnApplicationEvent_CountUpWithoutLockd_Test() {
        // 【1. 準備 (Given)】現在、過去の失敗回数が「2回」のアカウントを作成
        EmployeeAccount account = new EmployeeAccount();
        account.setName(testName);
        account.setFailedAttempts(2);
        account.setLockTime(null);

        // リポジトリに「"丸ちゃん"を探されたら、この2回失敗しているアカウントを返しなさい」と設定
        when(employeeAccountRepository.findByName(testName)).thenReturn(account);

        // 【2. 実行 (When)】テスト対象メソッドを呼び出します（パスワード間違いが発生したと仮定）
        listener.onApplicationEvent(event);

        // 【3. 検証 (Then)】
        // 失敗回数が 2 + 1 = 「3」に増えているか確認
        assertEquals(3, account.getFailedAttempts());
        // まだ5回未満なので、ロック日時がセットされず「null（空）」のままであるか確認
        assertNull(account.getLockTime());

        // データベースに失敗回数「3」を保存するSQLがちょうど1回実行されたかを検証
        verify(employeeAccountRepository, times(1)).updateLockStatus(account);
    }

    /**
     * 【テストケース2：ロック発動ルート（制限到達）】
     * 過去の失敗回数が「4回」溜まっており、今回の失敗でちょうど「5回目」に達した場合の挙動を検証します。
     * 
     * ＜期待される結果＞
     * 1. 失敗回数が 4 から 5 へ増えること
     * 2. 制限に達したため、ロック日時（lockTime）に「現在時刻（null以外）」が自動でセットされること
     * 3. ロック日時がセットされた状態のアカウント情報が、データベースに確実に1回保存されること
     */
    @Test
    @DisplayName("ログイン失敗で5回に達した場合、カウントアップされ、かつロック日時が設定されること")
    void OnApplicationEvent_LockWhenReachedFived_Test() {
        // 【1. 準備 (Given)】あと1回でアウトとなる、過去の失敗回数が「4回」のアカウントを作成
        EmployeeAccount account = new EmployeeAccount();
        account.setName(testName);
        account.setFailedAttempts(4);
        account.setLockTime(null);

        // リポジトリに「"丸ちゃん"を探されたら、この4回失敗しているアカウントを返しなさい」と設定
        when(employeeAccountRepository.findByName(testName)).thenReturn(account);

        // 【2. 実行 (When)】ログイン失敗イベントを発生させます（5回目のミス）
        listener.onApplicationEvent(event);

        // 【3. 検証 (Then)】
        // 失敗回数が「5」に達しているか確認
        assertEquals(5, account.getFailedAttempts());
        // ロック日時に、現在時刻のスタンプ（null以外の値）がしっかりと書き込まれたか確認
        assertNotNull(account.getLockTime());

        // ロックされた状態をデータベースに反映するSQLがちょうど1回実行されたかを検証
        verify(employeeAccountRepository, times(1)).updateLockStatus(account);
    }

    /**
     * 【テストケース3：安全対策ルート（ユーザー未存在）】
     * ログイン画面で「存在しないユーザー名」を入力してパスワードを間違えた場合の挙動を検証します。
     * 
     * ＜期待される結果＞
     * 1. データベースにいない人のデータは更新できないため、プログラムがバグ（NullPointerExceptionなど）でクラッシュしないこと
     * 2. 何も処理を行わず、安全にスルーしてメソッドが終了すること（DB更新も行われない）
     */
    @Test
    @DisplayName("ユーザーがデータベースに存在しない場合、何も処理を行わず安全に終了すること")
    void OnApplicationEvent_UserNotFoundd_Test() {
        // 【1. 準備 (Given)】リポジトリに「"丸ちゃん"を探されても、null（そんな人はいない）を返しなさい」と設定
        when(employeeAccountRepository.findByName(testName)).thenReturn(null);

        // 【2. 実行 ＆ 検証 (When & Then)】
        // メソッドを実行しても、途中でエラーにならずに安全に終了できるかテスト
        listener.onApplicationEvent(event);

        // 更新対象のデータがないため、データベースの更新処理（updateLockStatus）が「一度も呼ばれていないこと」を検証
        verify(employeeAccountRepository, never()).updateLockStatus(any());
    }
}
