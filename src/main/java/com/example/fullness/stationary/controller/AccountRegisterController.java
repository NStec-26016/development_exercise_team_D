package com.example.fullness.stationary.controller;

import com.example.fullness.stationary.form.AccountRegisterForm;
import com.example.fullness.stationary.entity.EmployeeAccount;
import com.example.fullness.stationary.service.AccountRegisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/account")
@SessionAttributes("form")
public class AccountRegisterController {

    private static final Logger logger = LoggerFactory.getLogger(AccountRegisterController.class);

    @Autowired
    private AccountRegisterService accountRegisterService;

    @ModelAttribute("form")
    public AccountRegisterForm setUpForm() {
        return new AccountRegisterForm();
    }

    /**
     * 1. 担当者アカウント登録(入力)画面の表示 (BP003)
     * URL: /admin/account/form (GET)
     */
    @GetMapping("/form")
    public String showForm(Model model, @ModelAttribute("form") AccountRegisterForm form) {
        try {
            // 未登録の社員情報を取得
            var unregisteredEmployees = accountRegisterService.getUnregisteredEmployees();
            model.addAttribute("employees", unregisteredEmployees);

            // ［例外処理仕様］未登録社員なし
            if (unregisteredEmployees.isEmpty()) {
                List<String> errors = new ArrayList<>();
                errors.add("アカウント登録可能な社員が存在しません");
                model.addAttribute("errorMessages", errors);
                // ※配布HTMLには確認ボタンを非活性化する th:disabled は付いていないため、
                // 上部エラーメッセージ表示で仕様を満たします
            }
        } catch (Exception e) {
            // ［例外処理仕様］社員データ取得エラー
            logger.error("社員情報の取得に失敗しました。詳細: ", e);
            List<String> errors = new ArrayList<>();
            errors.add("社員情報の取得に失敗しました");
            model.addAttribute("errorMessages", errors);
            model.addAttribute("employees", new ArrayList<>());
        }
        return "admin/account/form";
    }

    /**
     * 1.5 完了ボタン押下時のポスト処理
     * URL: /admin/account/form (POST)
     */
    @PostMapping("/form")
    public String validateForm(@Validated @ModelAttribute("form") AccountRegisterForm form,
            BindingResult result, Model model) {

        List<String> errorMessages = new ArrayList<>();

        // 1. 単体バリデーション（必須、文字数、半角英数字）のエラーをリストに詰め替える
        if (result.hasErrors()) {
            for (ObjectError error : result.getAllErrors()) {
                errorMessages.add(error.getDefaultMessage());
            }
        }

        // 2. アカウント名重複チェック（単体エラーがなく、入力がある場合のみ実行）
        if (!result.hasFieldErrors("accountName") && form.getAccountName() != null
                && !form.getAccountName().trim().isEmpty()) {
            if (accountRegisterService.isAccountNameDuplicate(form.getAccountName())) {
                errorMessages.add("このアカウント名は既に使用されています");
            }
        }

        // 3. 1つでもエラーがあれば、エラーリストを画面に渡して入力画面を再表示
        if (!errorMessages.isEmpty()) {
            try {
                // 再表示用に選択肢を再取得
                model.addAttribute("employees", accountRegisterService.getUnregisteredEmployees());
            } catch (Exception e) {
                logger.error("再表示時の社員情報取得に失敗しました", e);
            }
            // 配布HTMLの仕様「th:if="${errorMessages}"」に合わせてモデルに格納
            model.addAttribute("errorMessages", errorMessages);
            return "admin/account/form";
        }

        // 4. エラーがない場合、選択されたIDから社員名を取得して確認画面用に保存
        if (form.getEmployeeId() != null) {
            String empName = accountRegisterService.getEmployeeNameById(form.getEmployeeId());
            form.setEmployeeName(empName);
        }

        return "redirect:/admin/account/confirm";
    }

    /**
     * 2. 担当者アカウント登録(確認)画面の表示 (BP004)
     * URL: /admin/account/confirm (GET)
     */
    @GetMapping("/confirm")
    public String showConfirm(@ModelAttribute("form") AccountRegisterForm form,
            RedirectAttributes redirectAttributes) {

        // セッションタイムアウト・不正アクセスガード
        if (form.getAccountName() == null || form.getAccountName().trim().isEmpty() || form.getEmployeeId() == null) {
            List<String> errors = new ArrayList<>();
            errors.add("セッションが切れました。再度入力してください（入力情報が見つかりません。再度入力してください）");
            redirectAttributes.addFlashAttribute("errorMessages", errors);
            return "redirect:/admin/account/form";
        }

        return "admin/account/confirm";
    }

    /**
     * 2.5 確認画面からのポスト処理
     * URL: /admin/account/confirm (POST)
     */
    @PostMapping("/confirm")
    public String handleConfirmAction(@ModelAttribute("form") AccountRegisterForm form,
            @RequestParam("action") String action,
            RedirectAttributes redirectAttributes) {

        // [戻る]ボタン押下時
        if ("back".equals(action)) {
            return "redirect:/admin/account/form";
        }

        // [登録]ボタン押下時
        if ("register".equals(action)) {
            try {
                // 登録直前の競合対策（最終重複チェック）
                if (accountRegisterService.isAccountNameDuplicate(form.getAccountName())) {
                    List<String> errors = new ArrayList<>();
                    errors.add("このアカウント名は既に使用されています");
                    redirectAttributes.addFlashAttribute("errorMessages", errors);
                    return "redirect:/admin/account/form";
                }

                // 登録処理実行（パスワードの暗号化はService層でNoOpPasswordEncoderを自動使用）
                accountRegisterService.register(form);
                return "redirect:/admin/account/complete";

            } catch (Exception e) {
                // ［例外処理仕様］DB登録エラー
                logger.error("登録処理に失敗しました。詳細エラー: ", e);
                List<String> errors = new ArrayList<>();
                errors.add("登録処理に失敗しました。管理者に連絡してください");
                redirectAttributes.addFlashAttribute("errorMessages", errors);
                return "redirect:/admin/account/form";
            }
        }

        return "redirect:/admin/account/form";
    }

    /**
     * 3. 担当者アカウント登録(完了)画面の表示 (BP005)
     * URL: /admin/account/complete (GET)
     */
    @GetMapping("/complete")
    public String showComplete(@ModelAttribute("form") AccountRegisterForm form, SessionStatus sessionStatus) {

        // 不正アクセス・セッションデータ不足ガード
        if (form.getAccountName() == null || form.getAccountName().trim().isEmpty() || form.getEmployeeName() == null) {
            logger.warn("不正アクセスまたはセッション切れによる完了画面への直接アクセスを遮断しました。");
            return "redirect:/admin"; // 仕様に基づき、全員アクセス可能なメニュー画面へリダイレクト
        }

        // 💡 complete.html が画面を描画する（th:text="|${form.employeeName}...|）ためにセッションが必要なため、
        // 完了画面から「メニューへ」または「入力に戻る」で離脱したタイミング、もしくは次回のフォーム初期化時にクリアさせます。
        return "admin/account/complete";
    }
}