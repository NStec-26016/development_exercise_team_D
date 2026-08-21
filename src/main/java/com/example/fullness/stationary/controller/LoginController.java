package com.example.fullness.stationary.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ログイン画面および管理者用メニュー画面の遷移と制御を行うコントローラークラス。
 * 
 * 本クラスは、認証前のログイン画面表示、Spring Securityの認証プロセスにおいて発生した
 * 各種例外（認証不一致、アカウントロック、DB接続エラー等）に応じた動的なエラーメッセージの制御、
 * および認証成功後のトップメニュー画面への遷移制御を担当します。
 * 実際の認証判定、セッション管理、ログアウト等のセキュリティフィルタリング処理は、
 * Spring Securityのコンテキストによって自動処理されます。
 * 
 * @author Team_D 深堀
 * @version 1.0
 */
@Controller
public class LoginController {

    /**
     * ログイン画面（初期表示・ログインエラー時共通）を表示します。
     * 
     * URL「/admin/login」に対するGETリクエストを処理し、ビュー名 "admin/login" を返却します。
     * ログインに失敗してリダイレクト（パラメータに "error" が存在）された場合、セッションから
     * Spring Securityが保持する最新の例外オブジェクト（SPRING_SECURITY_LAST_EXCEPTION）を取得し、
     * 以下の条件に基づいてエラーメッセージ（errorMessage）を動的に切り替えます。
     * 
     * ■エラーメッセージ切り替え仕様
     * 1. 発生例外：LockedException を含む場合
     * - 画面表示：アカウントがロックされています。しばらく経ってから再度お試しください
     * - 処理内容：画面へのエラー表示のみ
     * 2.
     * 発生例外：AuthenticationServiceException、DataAccess、InternalAuthenticationServiceException
     * のいずれかを含む場合
     * - 画面表示：システムエラーが発生しました。管理者に連絡してください
     * - 処理内容：標準エラー出力に詳細ログを記録
     * 3. 発生例外：BadCredentialsException を含む場合（パスワード間違いなど）
     * - 画面表示：アカウント名またはパスワードが正しくありません
     * - 処理内容：標準出力に対象アカウントの失敗カウント用ログを記録
     * 
     * @param model   画面へデータを格納・送出するためのModelオブジェクト。
     *                画面引き渡し属性：
     *                - errorMessage: 条件判定されたエラーメッセージ文字列（通常初期表示時は null）
     *                - accountName: フォームの初期値用空文字（""）
     *                - loggedIn: 共通ヘッダー表示制御用フラグ（未ログインを示す false を固定）
     * @param error   ログイン失敗時にSpring Securityから付与されるリクエストパラメータ（"error"）。通常時は null
     * @param request クライアントからのHTTPリクエスト情報。セッションスコープからログイン例外を取得するために使用
     * @return ログイン画面のビュー名 "admin/login"
     */
    @RequestMapping(value = "/admin/login", method = { org.springframework.web.bind.annotation.RequestMethod.GET,
            org.springframework.web.bind.annotation.RequestMethod.POST })
    public String showLoginPage(
            org.springframework.ui.Model model,
            @org.springframework.web.bind.annotation.RequestParam(value = "error", required = false) String error,
            jakarta.servlet.http.HttpServletRequest request) {

        if (error != null) {
            // Spring Securityが自動で保存している「最後のログイン失敗理由（例外オブジェクト）」を取得
            Object lastException = request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");

            // デフォルトのメッセージ（例外が取得できなかった場合の安全策）
            String errorMsg = "アカウント名またはパスワードが正しくありません";

            if (lastException != null) {
                String exceptionClassName = lastException.getClass().getName();

                // 1️ アカウントロック時の判定
                if (exceptionClassName.contains("LockedException")) {
                    errorMsg = "アカウントがロックされています。しばらく経ってから再度お試しください";

                    // 2️ DB接続エラー・システムエラーの判定
                } else if (exceptionClassName.contains("AuthenticationServiceException")
                        || exceptionClassName.contains("DataAccess")
                        || exceptionClassName.contains("InternalAuthenticationServiceException")) {
                    errorMsg = "システムエラーが発生しました。管理者に連絡してください";

                    // エラーログに詳細情報を標準エラー出力に記録
                    System.err.println("[ERROR] ログイン処理中にシステム/DB接続エラーが発生しました。詳細: " + lastException);

                    // 3️ 認証情報不一致（パスワード間違いなど）の判定
                } else if (exceptionClassName.contains("BadCredentialsException")) {
                    errorMsg = "アカウント名またはパスワードが正しくありません";

                    // ログイン失敗回数を標準出力にログ記録
                    System.out.println(
                            "[INFO] 認証不一致によりログインが失敗しました。失敗回数をカウントします。対象: " + request.getParameter("accountName"));
                }
            }

            // 判定したメッセージをModelに格納（画面のerrorMessage属性とバインドされ、エラー領域が出現する）
            model.addAttribute("errorMessage", errorMsg);

        } else {
            // 通常初期表示時は null を渡すことで、Thymeleaf(th:if)によってエラー領域のタグごと非表示化する
            model.addAttribute("errorMessage", null);
        }

        // ⬇️ ⬇️ ⬇️ UC９で追加⬇️ ⬇️ ⬇️

        // 💡 最終解決：Spring Securityによって通常のログイン画面（GET）に強制リダイレクトされた際、
        // 直前まで操作していた「アカウント登録の確認画面（/admin/account/confirm）」からのセッション切れの痕跡を直接検知します。
        if (request.getHeader("referer") != null && request.getHeader("referer").contains("/admin/account/confirm")) {
            model.addAttribute("errorMessage", "セッションが切れました。再度入力してください");
        }

        // ⬆️ ⬆️ ⬆️UC９で追加⬆️ ⬆️ ⬆️

        model.addAttribute("accountName", "");
        model.addAttribute("loggedIn", false);
        return "admin/login";

    }

    /**
     * 管理者用メニュー（トップページ）画面を表示します。
     * 
     * URL「/admin」に対するGETリクエストを処理し、ビュー名 "admin/menu" を返却します。
     * Spring Securityのセキュリティコンテキストから現在の認証情報を解析し、
     * ログイン済み（認証成功）の状態であれば、ログイン中のユーザーアカウント名をModelに設定します。
     * 未ログイン状態で本URLへの直接アクセスが発生した場合は、ログインしていない状態のメニュー表示を維持します。
     *
     * @param model 画面へデータを格納・送出するためのModelオブジェクト。
     *              画面引き渡し属性：
     *              - loginEmployeeName: 認証されたユーザーのアカウント名文字列（ログイン時のみ設定）
     *              - loggedIn: 共通ヘッダーおよび画面要素の表示切り替えフラグ（ログイン済時: true、未ログイン時: false）
     * @return メニュー画面のビュー名 "admin/menu"
     */
    @GetMapping("/admin")
    public String showMenuPage(Model model) {

        // セキュリティコンテキストから認証情報を取得
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {

            // ログイン中のアカウント名を取得し、メニュー画面の「th:text="${loginEmployeeName}"」にマッピング
            String loginUserName = authentication.getName();
            model.addAttribute("loginEmployeeName", loginUserName);
            model.addAttribute("loggedIn", true);
        } else {
            // 未ログイン時はヘッダー部品（th:unless="${loggedIn}"）を「ログインボタン」に反応させる
            model.addAttribute("loggedIn", false);
        }

        return "admin/menu";
    }
}
