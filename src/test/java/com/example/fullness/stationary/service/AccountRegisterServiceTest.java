package com.example.fullness.stationary.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.fullness.stationary.entity.Employee;
import com.example.fullness.stationary.entity.EmployeeAccount;
import com.example.fullness.stationary.form.AccountRegisterForm;
import com.example.fullness.stationary.repository.EmployeeRepository;
import com.example.fullness.stationary.repository.EmployeeAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class AccountRegisterServiceTest {

    @InjectMocks
    private AccountRegisterService accountRegisterService;

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private EmployeeAccountRepository employeeAccountRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private AccountRegisterForm form;

    @BeforeEach
    void setUp() {
        form = new AccountRegisterForm();
        form.setEmployeeId("4");
        form.setAccountName("watanabe4");
        form.setPassword("00000");
    }

    /**
     * 項番1: getUnregisteredEmployees の正常系テスト
     */
    @Test
    void testGetUnregisteredEmployees_Success() {
        when(employeeRepository.findUnregisteredEmployees())
                .thenReturn(List.of(new Employee(), new Employee(), new Employee()));

        List<Employee> result = accountRegisterService.getUnregisteredEmployees();

        assertEquals(3, result.size());
        verify(employeeRepository).findUnregisteredEmployees();
    }

    /**
     * 項番2: getEmployeeNameById の正常系テスト
     */
    @Test
    void testGetEmployeeNameById_Success() {
        when(employeeRepository.findNameById(4)).thenReturn("渡辺太郎");

        assertEquals("渡辺太郎", accountRegisterService.getEmployeeNameById("4"));
    }

    /**
     * 項番3: isAccountNameDuplicate の正常系テスト
     */
    @Test
    void testIsAccountNameDuplicate_Success_NoDuplicate() {
        when(employeeAccountRepository.countByName("watanabe4")).thenReturn(0);

        assertFalse(accountRegisterService.isAccountNameDuplicate("watanabe4"));
    }

    /**
     * 項番4: register の正常系テスト
     */
    @Test
    void testRegister_Success() {
        when(passwordEncoder.encode("00000")).thenReturn("hashedPasswordXYZ");

        // 実行 & エラーが発生しないか検証
        assertDoesNotThrow(() -> accountRegisterService.register(form));

        verify(employeeAccountRepository).insertEmployeeAccount(any(EmployeeAccount.class)); // 💡 簡略化：times(1)を削除
    }
}
