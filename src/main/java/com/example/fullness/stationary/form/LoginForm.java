package com.example.fullness.stationary.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * ログイン画面の入力値を保持するフォームオブジェクト
 */
@Data // ゲッター、セッター、toString、equals、hashCodeが自動生成
public class LoginForm {

    /** アカウント名 */
    @NotBlank(message = "アカウント名を入力してください。")
    private String username;

    /** パスワード */
    @NotBlank(message = "パスワードを入力してください。")
    private String password;
}
