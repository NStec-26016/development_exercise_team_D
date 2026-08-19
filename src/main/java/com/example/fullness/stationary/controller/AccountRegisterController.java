package com.example.fullness.stationary.controller;

import com.example.fullness.stationary.form.AccountRegisterForm;
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
@SessionAttributes("form") // 配布HTMLの `${form.xxx}` とセッションを連動させる
public class AccountRegisterController {

    private static final Logger logger = LoggerFactory.getLogger(AccountRegisterController.class);

    @Autowired
    private AccountRegisterService accountRegisterService;

    /**
     * セッション上のフォームオブジェクトの初期化
     */
    @ModelAttribute("form")
    public AccountRegisterForm setUpForm() {
        return new AccountRegisterForm();
    }

    /**
     * 1. 担当者アカウント登録(入力)画面の表示
     * URL: /admin/account/form (GET)
     */
    @GetMapping("/form")
    public String showForm(Model model, @ModelAttribute("form") AccountRegisterForm form) {
        try {
            // 配布HTMLの ${employees} に合わせて、アカウント未登録の社員リストをModelに格納
            model.addAttribute("employees", accountRegisterService.getUnregisteredEmployees());

            // 【変更点の反映】「未登録社員なし」でもボタン非活性化フラグは送信しない（仕様削除のため）
            if (accountRegisterService.getUnregisteredEmployees().isEmpty()) {
                List<String> errors = new ArrayList<>();
                errors.add("アカウント登録可能な社員が存在しません");
                model.addAttribute("errorMessages", errors);
            }
        } catch (Exception e) {
            logger.error("社員情報の取得に失敗しました", e);
            List<String> errors = new ArrayList<>();
            errors.add("社員情報の取得に失敗しました");
            model.addAttribute("errorMessages", errors);
        }
        return "admin/account/form"; // templates/admin/account/form.html を呼び出す
    }

    /**
     * 1.5 入力画面からのポスト処理（完了ボタン押下時）
     * URL: /admin/account/form (POST)
     */
    @PostMapping("/form")
    public String validateForm(@Validated @ModelAttribute("form") AccountRegisterForm form,
            BindingResult result, Model model) {

        List<String> errorMessages = new ArrayList<>();

        // 必須入力や文字数・文字種（半角英数字）のバリデーションエラーをリストに詰める
        if (result.hasErrors()) {
            for (ObjectError error : result.getAllErrors()) {
                errorMessages.add(error.getDefaultMessage());
            }
        }

        // アカウント名重複チェック（入力がある場合のみ実行）
        if (form.getAccountName() != null && !form.getAccountName().isEmpty()) {
            if (accountRegisterService.isAccountNameDuplicate(form.getAccountName())) {
                errorMessages.add("このアカウント名は既に使用されています");
            }
        }

        // 1つでもエラーがあれば、エラーメッセージリストを保持して入力画面を再表示
        if (!errorMessages.isEmpty()) {
            model.addAttribute("errorMessages", errorMessages);
            // 選択肢の再セットが必要なため、showFormメソッドを内部呼び出しして画面を返す
            return showForm(model, form);
        }

        // 確認画面・完了画面の表示用に、選択されたIDから社員名を取得してFormにセット
        if (form.getEmployeeId() != null) {
            String empName = accountRegisterService.getEmployeeNameById(form.getEmployeeId());
            form.setEmployeeName(empName);
        }

        return "redirect:/admin/account/confirm";
    }

    /**
     * 2. 担当者担当者アカウント登録(確認)画面の表示
     * URL: /admin/account/confirm (GET)
     */
    @GetMapping("/confirm")
    public String showConfirm(@ModelAttribute("form") AccountRegisterForm form, Model model) {
        // セッションタイムアウトや不正な直接アクセスでデータが空の場合
        if (form.getAccountName() == null || form.getAccountName().isEmpty()) {
            List<String> errors = new ArrayList<>();
            errors.add("セッションが切れました。再度入力してください");
            model.addAttribute("errorMessages", errors);
            // 【変更点の反映】タイムアウト時の一律入力画面リダイレクト仕様は削除されたが、
            // 安全のためメッセージを保持して入力画面へ転送（forward）する形に留める
            return "forward:/admin/account/form";
        }
        return "admin/account/confirm"; // templates/admin/account/confirm.html を呼び出す
    }

    /**
     * 2.5 確認画面からのポスト処理（戻るボタン、または登録ボタン押下時）
     * URL: /admin/account/confirm (POST)
     */
    @PostMapping("/confirm")
    public String handleConfirmAction(@ModelAttribute("form") AccountRegisterForm form,
            @RequestParam("action") String action,
            RedirectAttributes redirectAttributes,
            Model model) {

        // 配布HTMLの name="action" value="back" （戻るボタン）が押された場合
        if ("back".equals(action)) {
            // セッションに値を残したまま入力画面へリダイレクト
            return "redirect:/admin/account/form";
        }

        // 配布HTMLの name="action" value="register" （登録ボタン）が押された場合
        if ("register".equals(action)) {
            try {
                // 登録直前の競合対策（最終重複チェック）
                if (accountRegisterService.isAccountNameDuplicate(form.getAccountName())) {
                    List<String> errors = new ArrayList<>();
                    errors.add("このアカウント名は既に使用されています");
                    redirectAttributes.addFlashAttribute("errorMessages", errors);
                    return "redirect:/admin/account/form";
                }

                // Service層でパスワードハッシュ化とDB登録を実行
                accountRegisterService.register(form);

                return "redirect:/admin/account/complete";

            } catch (Exception e) {
                logger.error("登録処理に失敗しました。管理者に連絡してください", e);
                List<String> errors = new ArrayList<>();
                errors.add("登録処理に失敗しました。管理者に連絡してください");
                redirectAttributes.addFlashAttribute("errorMessages", errors);
                return "redirect:/admin/account/form";
            }
        }

        return "redirect:/admin/account/form";
    }

    /**
     * 3. 担当者アカウント登録(完了)画面の表示
     * URL: /admin/account/complete (GET)
     */
    @GetMapping("/complete")
    public String showComplete(@ModelAttribute("form") AccountRegisterForm form, SessionStatus sessionStatus) {

        // 【変更点の反映】セッションデータ不足（データが空）の場合
        if (form.getAccountName() == null || form.getAccountName().isEmpty()) {
            // 変更前：トップ画面（FP001）へリダイレクト ➔ 変更後：メニュー画面（BP001）へリダイレクト
            // 【結合時の修正ポイント】配布HTMLのメニューへボタンに合わせて「/admin」にリダイレクトさせています。
            // ログインシステム側のメニュー画面のURLが異なる場合は、ここを書き換えてください。
            return "redirect:/admin";
        }

        // 【変更点の反映】不正アクセス時にエラー画面(FP000)へ飛ばすガードロジック行はすべて削除

        // 完了画面の表示が終わるタイミングでセッションをクリーンアップ
        sessionStatus.setComplete();

        return "admin/account/complete"; // templates/admin/account/complete.html を呼び出す
    }
}