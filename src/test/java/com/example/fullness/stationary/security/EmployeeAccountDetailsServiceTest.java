package com.example.fullness.stationary.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.fullness.stationary.entity.EmployeeAccount;
import com.example.fullness.stationary.repository.EmployeeAccountRepository;
import com.example.fullness.stationary.security.EmployeeAccountDetailsService;

@ExtendWith(MockitoExtension.class)
class EmployeeAccountDetailsServiceTest {

    @Mock
    private EmployeeAccountRepository employeeAccountRepository;

    @InjectMocks
    private EmployeeAccountDetailsService employeeAccountDetailsService;

    @Test
    @DisplayName("存在するユーザー名を指定した場合、その人の情報がが正しく返却されること")
    void loadUserByUsername_success() {
        // [1] テストデータの準備
        String username = "丸本 翔太郎";
        EmployeeAccount mockAccount = new EmployeeAccount();

        // [2] モックの動作定義（Repositoryが指定した名前で呼ばれたら、mockAccountを返す）
        when(employeeAccountRepository.findByName(username)).thenReturn(mockAccount);

        // [3] テスト対象メソッドの実行
        UserDetails result = employeeAccountDetailsService.loadUserByUsername(username);

        // [4] 結果の検証
        assertNotNull(result, "返却されたUserDetailsがnullでないこと");
        // ※実際の値チェックは、EmployeeAccountDetailsのゲッター実装に合わせて適宜調整してください

        // 権限の検証（現在の一律"Admin"が設定されているか確認）
        boolean hasAdminRole = result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("Admin"));
        assertTrue(hasAdminRole, "権限にAdminが含まれていること");

        // リポジトリのメソッドが1回呼ばれたことを検証
        verify(employeeAccountRepository, times(1)).findByName(username);
    }

    @Test
    @DisplayName("存在しないユーザー名を指定した場合、UsernameNotFoundExceptionが発生すること")
    void loadUserByUsername_userNotFound() {
        // [1] テストデータの準備
        String username = "存在しないユーザー";

        // [2] モックの動作定義（Repositoryがnullを返す）
        when(employeeAccountRepository.findByName(username)).thenReturn(null);

        // [3] テスト対象メソッドの実行と例外の検証
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            employeeAccountDetailsService.loadUserByUsername(username);
        });

        // エラーメッセージの検証
        assertEquals("user not found.", exception.getMessage());

        // リポジトリのメソッドが1回呼ばれたことを検証
        verify(employeeAccountRepository, times(1)).findByName(username);
    }
}
