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
     */
    @GetMapping("/form")
    public String showForm(Model model, @ModelAttribute("form") AccountRegisterForm form) {
        try {
            // プルダウン用の未登録社員一覧をモデルに設定
            model.addAttribute("employees", accountRegisterService.getUnregisteredEmployees());
            // 💡 配布HTMLの ${form.accountName}
            // 等が初回アクセス時に「無い」と怒られて500エラーになるのを防ぐため、明示的にモデルに渡します
            model.addAttribute("form", form);
        } catch (Exception e) {
            logger.error("社員情報の取得に失敗しました。詳細: ", e);
            model.addAttribute("errorMessages", List.of("社員情報の取得に失敗しました"));
            model.addAttribute("employees", new ArrayList<>());
        }
        return "admin/account/form";
    }

    /**
     * 1.5 入力画面からのポスト（完了ボタン押下時）
     */
    @PostMapping("/form")
    public String validateForm(@Validated @ModelAttribute("form") AccountRegisterForm form,
            BindingResult result, Model model) {

        List<String> errorMessages = new ArrayList<>();

        if (result.hasErrors()) {
            for (ObjectError error : result.getAllErrors()) {
                errorMessages.add(error.getDefaultMessage());
            }
        }

        // アカウント名重複チェック
        if (!result.hasFieldErrors("accountName") && form.getAccountName() != null
                && !form.getAccountName().trim().isEmpty()) {
            if (accountRegisterService.isAccountNameDuplicate(form.getAccountName())) {
                errorMessages.add("このアカウント名は既に使用されています");
            }
        }

        /// エラーがあれば入力画面へ戻す
        if (!errorMessages.isEmpty()) {
            model.addAttribute("errorMessages", errorMessages);
            model.addAttribute("employees", accountRegisterService.getUnregisteredEmployees());
            model.addAttribute("form", form);
            return "admin/account/form";
        }

        // 💡 正常時に選択されたIDから「社員の名前」を取得してフォームに退避（これでconfirm.htmlの500エラーが消えます）
        if (form.getEmployeeId() != null) {
            String empName = accountRegisterService.getEmployeeNameById(form.getEmployeeId());
            form.setEmployeeName(empName);
        }

        return "redirect:/admin/account/confirm";
    }

    /**
     * 2. 担当者アカウント登録(確認)画面の表示 (BP004)
     */
    @GetMapping("/confirm")
    public String showConfirm(@ModelAttribute("form") AccountRegisterForm form,
            RedirectAttributes redirectAttributes) {

        if (form.getAccountName() == null || form.getAccountName().trim().isEmpty() || form.getEmployeeId() == null
                || form.getEmployeeId().trim().isEmpty()) {
            List<String> errors = List.of("セッションが切れました。再度入力してください");
            redirectAttributes.addFlashAttribute("errorMessages", errors);
            return "redirect:/admin/account/form";
        }

        return "admin/account/confirm";
    }

    /**
     * 2.5 確認画面からのポスト
     */
    @PostMapping("/confirm")
    public String handleConfirmAction(@ModelAttribute("form") AccountRegisterForm form,
            @RequestParam("action") String action,
            RedirectAttributes redirectAttributes) {

        if ("back".equals(action)) {
            return "redirect:/admin/account/form";
        }

        if ("register".equals(action)) {
            try {
                if (accountRegisterService.isAccountNameDuplicate(form.getAccountName())) {
                    redirectAttributes.addFlashAttribute("errorMessages", List.of("このアカウント名は既に使用されています"));
                    return "redirect:/admin/account/form";
                }

                // 登録実行
                accountRegisterService.register(form);
                return "redirect:/admin/account/complete";

            } catch (Exception e) {
                logger.error("登録処理に失敗しました。詳細エラー: ", e);
                redirectAttributes.addFlashAttribute("errorMessages", List.of("登録処理に失敗しました。管理者に連絡してください"));
                return "redirect:/admin/account/form";
            }
        }

        return "redirect:/admin/account/form";
    }

    /**
     * 3. 担当者アカウント登録(完了)画面の表示 (BP005)
     */
    @GetMapping("/complete")
    public String showComplete(@ModelAttribute("form") AccountRegisterForm form) {
        if (form.getAccountName() == null || form.getAccountName().trim().isEmpty()) {
            logger.warn("不正アクセス検知: 完了画面への直接アクセスを遮断しました。");
            return "redirect:/admin";
        }
        return "admin/account/complete";
    }

    @GetMapping("/reset")
    public String resetForm(SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return "redirect:/admin/account/form";
    }
}
