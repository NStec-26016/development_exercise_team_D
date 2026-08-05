package com.example.fullness.stationary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.fullness.stationary.form.LoginForm;

// @Controller
// @RequestMapping("exe01")
/**
 * <b>概要：ログイン・メニュー画面遷移制御コントローラー</b><br>
 * メニュー画面からログイン画面への遷移、およびログイン認証後のリダイレクト処理を制御します。
 * 
 * @author Team_D 深堀
 */
public class LoginController {

    /**
     * ログイン画面の表示
     * 
     * @param error ログイン失敗時にURLに付与される "?error" を検知するパラメータ
     * @return login.htmlを表示
     */
    @GetMapping("/login")
    public String showLoginForm(@ModelAttribute LoginForm form,
            @RequestParam(value = "error", required = false) String error,
            Model model) {

        // 💡 要件2：アカウント名又はパスワードが間違っていた場合（SecurityConfigのfailureUrlから遷移）
        if (error != null) {
            model.addAttribute("errorMessage", "アカウント名又はパスワードが間違っています。");
        }

        return "login";
    }

    /**
     * メニュー画面の表示
     */
    @GetMapping("/menu")
    public String showMenu() {
        return "menu"; // menu.htmlを表示（ログインしていないとSecurityConfigに弾かれます）
    }

}
