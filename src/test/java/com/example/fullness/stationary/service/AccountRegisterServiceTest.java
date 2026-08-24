package com.example.fullness.stationary.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.fullness.stationary.entity.Employee;
import com.example.fullness.stationary.form.AccountRegisterForm;
import com.example.fullness.stationary.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class AccountRegisterServiceTest {

    @InjectMocks
    private AccountRegisterService accountRegisterService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AccountRegisterForm form;

    @BeforeEach
    void setUp() {
        // 正常系登録用の共通フォーム：ID4の渡辺太郎をセット
        form = new AccountRegisterForm();
        form.setEmployeeId("4");
        form.setAccountName("watanabe4");
        form.setPassword("00000");
    }

    /**
     * 項番1: getUnregisteredEmployees の正常系テスト
     * 期待値: 未登録社員（ID4, ID5, ID6）の合計3件がリストとして返却されること
     */
    @Test
    void testGetUnregisteredEmployees_Success() {
        // モックの設定：ID1〜3は登録済みの想定なので、未登録の3人分をダミー作成して返す
        List<Employee> unregisteredList = new ArrayList<>();

        Employee emp4 = new Employee(); // 渡辺太郎のダミー
        Employee emp5 = new Employee();
        Employee emp6 = new Employee();

        unregisteredList.add(emp4);
        unregisteredList.add(emp5);
        unregisteredList.add(emp6);

        when(employeeRepository.findUnregisteredEmployees()).thenReturn(unregisteredList);

        // 実行
        List<Employee> result = accountRegisterService.getUnregisteredEmployees();

        // 検証 (expected = 3)
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(employeeRepository, times(1)).findUnregisteredEmployees();
    }

    /**
     * 項番2: getEmployeeNameById の正常系テスト
     * 期待値: 引数に "4" を渡した際、文字列 "渡辺太郎" が返却されること
     */
    @Test
    void testGetEmployeeNameById_Success() {
        // モックの設定: ID「4」が渡されたときに「渡辺太郎」を返すように設定
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), eq(4)))
                .thenReturn("渡辺太郎");

        // 実行
        String employeeName = accountRegisterService.getEmployeeNameById("4");

        // 検証 (expected = "渡辺太郎")
        assertEquals("渡辺太郎", employeeName);
    }

    /**
     * 項番3: isAccountNameDuplicate の正常系テスト
     * 期待値: 新規アカウント名 "watanabe4" は重複していないため、false が返却されること
     */
    @Test
    void testIsAccountNameDuplicate_Success_NoDuplicate() {
        // モックの設定: COUNT(*) のSQLが実行された際、0件（重複なし）を返すように設定
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("watanabe4")))
                .thenReturn(0);

        // 実行
        boolean isDuplicate = accountRegisterService.isAccountNameDuplicate("watanabe4");

        // 検証 (expected = false)
        assertFalse(isDuplicate);
    }

    /**
     * 項番4: register の正常系テスト
     * 期待値: 例外が発生せず正常終了し、DBにID4のデータ保存命令（update）が1回送信されること
     */
    @Test
    void testRegister_Success() {
        // モックの設定: パスワード暗号化結果をダミー化
        when(passwordEncoder.encode("00000")).thenReturn("hashedPasswordXYZ");

        // モックの設定: INSERTのSQLが走った際、成功（1行更新）を返すように設定
        when(jdbcTemplate.update(anyString(), eq(4), eq("watanabe4"), eq("hashedPasswordXYZ")))
                .thenReturn(1);

        // 実行 & エラーが発生しないか検証
        assertDoesNotThrow(() -> accountRegisterService.register(form));

        // 検証: 引数に「4」がガッチリ含まれた状態で、jdbcTemplate.update が確実に「1回」呼ばれたか確認
        verify(jdbcTemplate, times(1)).update(anyString(), eq(4), eq("watanabe4"), eq("hashedPasswordXYZ"));
    }
}
