package com.example.fullness.stationary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.fullness.stationary.form.CategoryForm;
import com.example.fullness.stationary.service.CategoryService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品カテゴリ登録機能の画面遷移および例外処理を制御するコントローラークラス。
 * 
 * <p>
 * 【画面遷移フロー】
 * </p>
 * 
 * <pre>
 * 1. 入力画面(BP019) -> 2. バリデーションチェック -> 3. 確認画面(BP020) -> 4. 登録完了画面(BP021)
 * </pre>
 * 
 * <p>
 * 【実装した主な機能】
 * </p>
 * <ul>
 * <li>単項目バリデーションチェック（未入力・文字数制限）</li>
 * <li>データベース連携によるカテゴリ名の重複チェック</li>
 * <li>ブラウザの直接URL入力に対する不正アクセス防止リダイレクト</li>
 * <li>セッション消失時やデータベース登録失敗時の堅牢なエラーハンドリング</li>
 * </ul>
 * 
 * @author フルネス文具 開発チームD
 * @version 1.0
 */
@Controller
@RequestMapping("admin/category")
public class CategoryController {

    /** カテゴリ登録に関するビジネスロジックを担当するサービス */
    private final CategoryService categoryService;

    /**
     * コンストラクタによる依存性の注入（DI）。
     * 
     * @param categoryService カテゴリサービス
     */
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 1. 商品カテゴリ登録入力画面(BP019)の初期表示。
     * 
     * <p>
     * 【処理内容】
     * </p>
     * <ul>
     * <li>ヘッダーのメニュー制御用にログイン状態(loggedIn)を画面に送信します。</li>
     * <li>他メソッドからのエラー時リダイレクトでない場合のみ、画面初期化用の空Formを生成します。</li>
     * </ul>
     * 
     * @param principal ログインユーザー情報
     * @param model     画面へデータを渡すためのモデル
     * @return 商品カテゴリ登録入力画面のHTMLパス (admin/category/form)
     */
    @GetMapping("add")
    public String showAddForm(Principal principal, Model model) {
        model.addAttribute("loggedIn", principal != null);

        // エラーリダイレクト（フラッシュ属性引き継ぎ）のメッセージを上書き消去しないための防御策
        if (!model.containsAttribute("errorMessages")) {
            model.addAttribute("form", new CategoryForm());
        }
        return "admin/category/form";
    }

    /**
     * 2. 入力画面(BP019)で「完了」ボタンが押されたときのバリデーションおよび重複チェック。
     * 
     * <p>
     * 【処理内容】
     * </p>
     * <ol>
     * <li>LombokおよびJakartaアノテーションによる単項目チェック(NotBlank等)を実施。</li>
     * <li>単項目エラーが無い場合のみ、Serviceを呼び出してDB内の重複チェックを実施。</li>
     * <li>エラーが発生した場合は、メッセージ一覧を保持して入力画面(form)へ差し戻す。</li>
     * <li>すべてのチェックを通過した場合、確認画面(BP020)のHTMLを表示する。</li>
     * </ol>
     * 
     * @param principal     ログインユーザー情報
     * @param form          入力されたカテゴリ名が格納されたFormオブジェクト
     * @param bindingResult バリデーション結果
     * @param model         画面へデータを渡すためのモデル
     * @return エラー時は入力画面、正常時は確認画面のHTMLパス
     */
    @PostMapping("add")
    public String verify(
            Principal principal,
            @Validated @ModelAttribute("form") CategoryForm form,
            BindingResult bindingResult,
            Model model) {

        model.addAttribute("loggedIn", principal != null);

        List<String> errorMessages = new ArrayList<>();

        // ❶ 単項目（未入力・文字数など）のエラー検出とメッセージ詰め替え
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                errorMessages.add(error.getDefaultMessage());
            }
        }

        // ❷ 基本チェック通過時のみ、データベースの重複チェックを限定発動（サーバー負荷軽減）
        if (errorMessages.isEmpty()) {
            if (categoryService.isCategoryNameExists(form.getName())) {
                errorMessages.add("入力されたカテゴリ名は既に登録されています。");
            }
        }

        // ❸ 何かしらのエラーが検出された場合は入力画面へ差し戻す
        if (!errorMessages.isEmpty()) {
            model.addAttribute("errorMessages", errorMessages);
            return "admin/category/form";
        }

        return "admin/category/confirm";
    }

    /**
     * 画面設計書（不正アクセス）対応：確認画面のURLに直接アクセスされたときの防御策。
     * 
     * <p>
     * 【処理内容】
     * </p>
     * 入力画面を経由せずにブラウザのURL欄から直接確認画面のURLをGET要求された場合、
     * 405エラー画面を出さずに、仕様書の指示通り即座に入力画面（BP019）へ無言でリダイレクトします。
     * 
     * @return 入力画面(add)へのリダイレクト命令
     */
    @GetMapping("confirm")
    public String handleDirectAccess() {
        return "redirect:/admin/category/add";
    }

    /**
     * 3. 確認画面(BP020)で「登録」または「戻る」ボタンが押されたときの最終処理。
     * 
     * <p>
     * 【処理内容】
     * </p>
     * <ul>
     * <li>「戻る」ボタン押下時：入力値を保持したまま入力画面へフォワード。</li>
     * <li>「登録」ボタン押下時：Javaプログラムによる安全なnullチェック、およびDB登録処理。</li>
     * <li>二重送信防止ルール(PRGパターン)に従い、保存処理完了後は必ず完了画面URLへリダイレクト。</li>
     * </ul>
     * 
     * @param principal          ログインユーザー情報
     * @param action             押されたボタンを識別するためのパラメータ (back または register)
     * @param form               確認画面の裏で保持しているフォームデータ
     * @param redirectAttributes リダイレクト先へフラッシュ属性としてデータを安全に運ぶための部品
     * @param model              画面へデータを渡すためのモデル
     * @return エラー時/戻る時は入力画面、登録成功時は登録完了画面へのリダイレクトパス
     */
    @PostMapping("add/confirm")
    public String confirm(
            Principal principal,
            @RequestParam("action") String action,
            @ModelAttribute("form") CategoryForm form,
            RedirectAttributes redirectAttributes,
            Model model) {

        model.addAttribute("loggedIn", principal != null);

        // 「戻る」ボタンが押された場合は、入力フォーム画面へ戻す
        if ("back".equals(action)) {
            return "admin/category/form";
        }

        // ❶ 【今日追加した重要チェック】：データがnull、または文字が空の状態で直接アクセスを試みた場合
        if (form == null || form.getName() == null || form.getName().trim().isEmpty()) {
            List<String> errorMessages = new ArrayList<>();
            errorMessages.add("不正なアクセスです。");

            // 安全なフラッシュ属性としてメッセージを仕込み、入力画面へリダイレクト
            redirectAttributes.addFlashAttribute("errorMessages", errorMessages);
            return "redirect:/admin/category/add";
        }

        // ❷ データベース登録処理とシステムエラー(try-catch)対策
        try {
            categoryService.registerCategory(form.getName());

        } catch (Exception e) {
            e.printStackTrace(); // サーバーコンソールに例外ログを出力
            List<String> errorMessages = new ArrayList<>();
            errorMessages.add("登録に失敗しました。");

            // 画面を500エラーで真っ白にさせず、エラー文を抱えて入力画面へ強制送還
            redirectAttributes.addFlashAttribute("errorMessages", errorMessages);
            return "redirect:/admin/category/add";
        }

        // 正常終了：完了画面に登録されたカテゴリ名を表示するためにフラッシュ属性へ格納
        redirectAttributes.addFlashAttribute("categoryName", form.getName());
        return "redirect:/admin/category/complete";
    }

    /**
     * 4. 商品カテゴリ登録完了画面(BP021)を表示する処理。
     * 
     * <p>
     * 【処理内容】
     * </p>
     * 直前のリダイレクト元（confirmメソッドの登録処理）から受け取った
     * 登録カテゴリ名(categoryName)をThymeleafへ橋渡しし、安全に完了画面を表示します。
     * 
     * @param principal    ログインユーザー情報
     * @param categoryName 登録が成功したカテゴリの名前
     * @param model        画面へデータを渡すためのモデル
     * @return 登録完了画面のHTMLパス (admin/category/complete)
     */
    @GetMapping("complete")
    public String showCompletePage(
            Principal principal,
            @ModelAttribute("categoryName") String categoryName,
            Model model) {

        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("categoryName", categoryName);

        return "admin/category/complete";
    }

    /**
     * 5. 例外処理仕様：セッションデータ不足（不正アクセス）を自動検知したときの専用フック。
     * 
     * <p>
     * 【処理内容】
     * </p>
     * 画面遷移中にブラウザバックの乱用やクッキーの消去などにより「セッションデータ不足」が発生した場合、
     * Spring Bootが投げる例外をこのメソッドが自動的にインターセプト（横取り）します。
     * 仕様書のルールに従い、「不正なアクセスです。」とメッセージを出し、入力画面(BP019)へ完全リダイレクトします。
     * 
     * @param redirectAttributes リダイレクト先へフラッシュ属性を運ぶための部品
     * @return 入力画面(add)へのリダイレクト命令
     */
    @ExceptionHandler(HttpSessionRequiredException.class)
    public String handleSessionError(RedirectAttributes redirectAttributes) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add("不正なアクセスです。");

        redirectAttributes.addFlashAttribute("errorMessages", errorMessages);
        return "redirect:/admin/category/add";
    }
}
