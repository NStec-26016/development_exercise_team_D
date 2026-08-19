package com.example.fullness.stationary.form;

import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AccountRegisterForm implements Serializable {
    private static final long serialVersionUID = 1L;

    // ［社員名］必須選択チェック
    @NotNull(message = "社員名を選択してください")
    @NotBlank(message = "社員名を選択してください")
    private String employeeId;

    // 表示用：選択された社員の名前を保持する（確認・完了画面用）
    private String employeeName;

    // ［アカウント名］各種バリデーション
    @NotBlank(message = "アカウント名を入力してください")
    @Size(min = 5, max = 20, message = "アカウント名は5～20文字で入力してください")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "アカウント名は半角英数字で入力してください")
    private String accountName;

    // ［パスワード］各種バリデーション
    @NotBlank(message = "パスワードを入力してください")
    @Size(min = 5, max = 20, message = "パスワードは5～20文字で入力してください")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "パスワードは半角英数字で入力してください")
    private String password;

    // ゲッター・セッター
    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
