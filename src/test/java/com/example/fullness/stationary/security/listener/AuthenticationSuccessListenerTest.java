package com.example.fullness.stationary.security.listener;

import com.example.fullness.stationary.entity.EmployeeAccount;
import com.example.fullness.stationary.repository.EmployeeAccountRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationSuccessListenerTest {

    @InjectMocks
    private AuthenticationSuccessListener listener;

    @Mock
    private EmployeeAccountRepository employeeAccountRepository;

    @Mock
    private AuthenticationSuccessEvent event;

    @Mock
    private Authentication authentication;

    private final String testName = "marumoto";

    @BeforeEach
    void setUp() {
        // イベントから認証情報、認証情報からユーザー名が取得できるモックの動作を設定
        lenient().when(event.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(testName);
    }

    @Test
    @DisplayName("過去に失敗履歴がある場合、回数が0になりロック日時がクリアされてDBが更新されること")
    void testOnApplicationEvent_ResetSuccess() {
        // 準備: 失敗回数が3回、ロック日時が入っているアカウントを用意
        EmployeeAccount account = new EmployeeAccount();
        account.setName("marumoto");
        account.setFailedAttempts(3);
        account.setLockTime(LocalDateTime.now());

        when(employeeAccountRepository.findByName("marumoto")).thenReturn(account);

        // 実行
        listener.onApplicationEvent(event);

        // 検証: 値がリセットされているか
        assertEquals(0, account.getFailedAttempts());
        assertNull(account.getLockTime());

        // 検証: リポジトリの更新メソッドが1回だけ呼ばれたか
        verify(employeeAccountRepository, times(1)).updateLockStatus(account);
    }

    @Test
    @DisplayName("過去に失敗履歴がない場合、DBの更新処理が呼び出されないこと")
    void testOnApplicationEvent_NoActionNeeded() {
        // 準備: 失敗回数が0回、ロック日時も無しの綺麗なアカウントを用意
        EmployeeAccount account = new EmployeeAccount();
        account.setName("marumoto");
        account.setFailedAttempts(0);
        account.setLockTime(null);

        when(employeeAccountRepository.findByName("marumoto")).thenReturn(account);

        // 実行
        listener.onApplicationEvent(event);

        // 検証: 値が0のままであること
        assertEquals(0, account.getFailedAttempts());
        assertNull(account.getLockTime());

        // 検証: 無駄なDB更新（updateLockStatus）が呼ばれていないこと
        verify(employeeAccountRepository, never()).updateLockStatus(any());
    }

    @Test
    @DisplayName("ユーザーがデータベースに存在しない場合、何も処理を行わずエラーにならないこと")
    void testOnApplicationEvent_UserNotFound() {
        // 準備: 該当ユーザーがDBから見つからない（nullが返る）状態を再現
        when(employeeAccountRepository.findByName("marumoto")).thenReturn(null);

        // 実行 & 検証: エラーが発生せずに終了すること
        listener.onApplicationEvent(event);

        // 検証: 更新処理が呼ばれていないこと
        verify(employeeAccountRepository, never()).updateLockStatus(any());
    }
}
//
