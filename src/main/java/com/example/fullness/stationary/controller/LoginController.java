package com.example.fullness.stationary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * ログインおよびメニュー画面の画面遷移を制御するコントローラークラス。
 */
@Controller
public class LoginController {

    /**
     * ブラウザにメニュー画面を表示します。
     * <p>
     * URL「/menu」にアクセスした際に、templates/menu.html を呼び出します。
     * </p>
     *
     * @param model 画面にデータを渡すためのオブジェクト
     * @return 遷移先HTMLのファイル名 "menu"
     */
    @GetMapping("/menu")
    public String showMenuPage(Model model) {

        // 💡テスト用に、画面（menu.html）の ${loginUserName} に表示する仮の名前をセットします
        model.addAttribute("loginUserName", "テストユーザー");

        return "menu"; // templates/menu.html を呼び出す
    }

    // ログインボタン（POST）を受け取る窓口
    @PostMapping("/login") // 👈 ここが @PostMapping になっていますか？
    public String loginTest(Model model) {
        model.addAttribute("loginUserName", "山田 太郎（テストログイン）");
        return "menu";
    }

    // 💡 1. ブラウザで /login と打ち込んだときに、ログイン画面を表示する窓口（今回追加）
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // src/main/resources/templates/login.html を呼び出す
    }
}
