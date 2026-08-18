package com.example.fullness.stationary.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ログインおよびメニュー画面の画面遷移を制御するコントローラークラス。
 */
@Controller
public class LoginController {

    /**
     * ログイン画面を表示します。
     * 💡【修正】：エラーの詳細理由を判定するために、引数に「jakarta.servlet.http.HttpServletRequest」を追加しています。
     */
    @GetMapping("/admin/login")
    public String showLoginPage(
            org.springframework.ui.Model model,
            @org.springframework.web.bind.annotation.RequestParam(value = "error", required = false) String error,
            jakarta.servlet.http.HttpServletRequest request) { // 💡 エラー詳細を取得するために追記

        if (error != null) {
            // 💡 Spring Securityが自動で保存している「最後のログイン失敗理由（例外オブジェクト）」を取得します
            Object lastException = request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");

            // デフォルトのメッセージ（例外が取れなかった場合の安全策）
            String errorMsg = "アカウント名またはパスワードが正しくありません";

            if (lastException != null) {
                String exceptionClassName = lastException.getClass().getName();

                // 1️⃣ アカウントロック時の判定（LockedExceptionなど）
                if (exceptionClassName.contains("LockedException")) {
                    errorMsg = "アカウントがロックされています。しばらく経ってから再度お試しください";

                    // 2️⃣ DB接続エラー・システムエラーの判定（InternalAuthenticationServiceException や
                    // DataAccessResouce系など）
                } else if (exceptionClassName.contains("AuthenticationServiceException")
                        || exceptionClassName.contains("DataAccess")
                        || exceptionClassName.contains("InternalAuthenticationServiceException")) {
                    errorMsg = "システムエラーが発生しました。管理者に連絡してください";

                    // 💡 エラーログに詳細情報を記録（コンソールおよびログファイルに出力されます）
                    System.err.println("[ERROR] ログイン処理中にシステム/DB接続エラーが発生しました。詳細: " + lastException);

                    // 3️⃣ 認証情報不一致（BadCredentialsException：パスワード間違いなど）
                } else if (exceptionClassName.contains("BadCredentialsException")) {
                    errorMsg = "アカウント名またはパスワードが正しくありません";

                    // 💡 ログイン失敗回数をカウント（コンソール等に記録、実務ではここでServiceを呼び出してDBのカウントを+1します）
                    System.out.println(
                            "[INFO] 認証不一致によりログインが失敗しました。失敗回数をカウントします。対象: " + request.getParameter("accountName"));
                }
            }

            // 判定したメッセージをModelにセット（ピンクの枠が出現します）
            model.addAttribute("errorMessage", errorMsg);

        } else {
            // 通常時（最初に画面を開いたとき）は null を渡してピンクの枠ごと消し去ります
            model.addAttribute("errorMessage", null);
        }

        model.addAttribute("accountName", "");
        model.addAttribute("loggedIn", false);
        return "admin/login";
    }

    /**
     * メニュー画面を表示します。
     */
    @GetMapping("/admin")
    public String showMenuPage(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {
            String loginUserName = authentication.getName();
            model.addAttribute("loginEmployeeName", loginUserName);
            model.addAttribute("loggedIn", true);
        } else {
            model.addAttribute("loggedIn", false);
        }

        return "admin/menu";
    }
}
