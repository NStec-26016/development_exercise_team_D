package com.example.fullness.stationary.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Data;

@Data
public class AccountRegisterForm implements Serializable {

    private static final long serialVersionUID = 1L;

    // 社員選択（必須チェック）
    @NotBlank(message = "社員名を選択してください")
    private String employeeId;

    // 画面表示・保持用の社員名
    private String employeeName;

    // アカウント名（必須、5〜20文字、半角英数字）
    @NotBlank(message = "アカウント名を入力してください")
    @Size(min = 5, max = 20, message = "アカウント名は5～20文字で入力してください")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "アカウント名は半角英数字で入力してください")
    private String accountName;

    // パスワード（必須、5〜20文字、半角英数字）
    @NotBlank(message = "パスワードを入力してください")
    @Size(min = 5, max = 20, message = "パスワードは5～20文字で入力してください")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "パスワードは半角英数字で入力してください")
    private String password;
}
