package com.example.fullness.stationary.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ログインおよびメニュー画面の画面遷移を制御するコントローラークラス。
 * <p>
 * 本クラスは、認証画面（Login）の表示制御、および認証成功後のメニュー画面（Menu）の表示制御を行います。
 * 実際の認証判定、セッション管理、ログアウトのロジックはSpring Securityのフィルター層によって自動処理されます。
 * </p>
 * 
 * @author Team_D 深堀
 */
@Controller
public class LoginController {

    /**
     * ログイン画面を表示します。
     * <p>
     * URL「/admin/login」へのGETリクエストに対して、ログイン用HTML（templates/login.html）を返却します。
     * </p>
     *
     * @return ログイン画面のビュー名 "login"
     */
    @GetMapping("/admin/login")
    public String showLoginPage() {
        return "login";
    }

    /**
     * 
     * 
     * メニュー画面を表示します。
     * <p>
     * URL「/admin」へのGETリクエストを処理します。
     * Spring Securityのコンテキストから現在ログイン中の認証情報を取得し、
     * ログイン済みであればユーザー名を画面（Model）に設定します。
     * 未ログインの場合は、ユーザー名を設定せずに画面を表示します。
     * </p>
     *
     * @param model 画面へデータを渡すためのModelオブジェクト
     * @return メニュー画面のビュー名 "menu"
     */
    @GetMapping("/admin")
    public String showMenuPage(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {
            String loginUserName = authentication.getName();
            model.addAttribute("name", loginUserName);
        }

        return "menu";
    }
}
