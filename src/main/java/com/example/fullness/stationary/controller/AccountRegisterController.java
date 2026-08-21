package com.example.fullness.stationary.controller;

import com.example.fullness.stationary.entity.Employee;
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
     * メニュー画面（menu.html）と同じヘッダー表示を維持するための共通処理
     */
    @ModelAttribute
    public void addLoginStatusToModel(Model model) {
        model.addAttribute("loggedIn", true);
        model.addAttribute("loginEmployeeName", "管理者（ログイン中）");
    }

    /**
     * 1. 担当者アカウント登録(入力)画面の表示 (BP003)
     * 💡 メニュー等からの新規アクセス時はセッションをクリアして空欄化します。
     */
    @GetMapping("/form")
    public String showForm(Model model, @ModelAttribute("form") AccountRegisterForm form,
            SessionStatus sessionStatus, jakarta.servlet.http.HttpServletRequest request) {
        try {
            // 直前の画面（Refererヘッダー）が「確認画面（/confirm）」だった場合は、ユーザーが［戻る］操作をしたと判定します。
            // この時はセッションを消去せず、保持されているデータをModelに再セットして画面に戻します。
            String referer = request.getHeader("referer");
            boolean isBackFromConfirm = (referer != null && referer.contains("/admin/account/confirm"));

            if (isBackFromConfirm) {
                model.addAttribute("employees", accountRegisterService.getUnregisteredEmployees());
                model.addAttribute("form", form);
                return "admin/account/form";
            }

            // メニューから新規で開いたとき（キャンセル後など）は完全初期化処理を行います
            sessionStatus.setComplete();
            form = new AccountRegisterForm();

            List<Employee> employees = accountRegisterService.getUnregisteredEmployees();

            if (employees == null || employees.isEmpty()) {
                model.addAttribute("infoMessage", "アカウント登録可能な社員が存在しません");
                model.addAttribute("employees", new ArrayList<>());
            } else {
                model.addAttribute("employees", employees);
            }

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

        if (form.getEmployeeId() != null && !form.getEmployeeId().trim().isEmpty()) {
            String empName = accountRegisterService.getEmployeeNameById(form.getEmployeeId());
            form.setEmployeeName(empName);
        }

        List<String> errorMessages = new ArrayList<>();

        if (result.hasErrors()) {
            for (ObjectError error : result.getAllErrors()) {
                errorMessages.add(error.getDefaultMessage());
            }
        }

        if (!result.hasFieldErrors("accountName") && form.getAccountName() != null
                && !form.getAccountName().trim().isEmpty()) {
            if (accountRegisterService.isAccountNameDuplicate(form.getAccountName())) {
                errorMessages.add("このアカウント名は既に使用されています");
            }
        }

        if (!errorMessages.isEmpty()) {
            model.addAttribute("errorMessages", errorMessages);
            model.addAttribute("employees", accountRegisterService.getUnregisteredEmployees());
            model.addAttribute("form", form);
            return "admin/account/form";
        }

        return "redirect:/admin/account/confirm";
    }

    /**
     * 2. 担当者アカウント登録(確認)画面の表示 (BP004)
     */
    @GetMapping("/confirm")
    public String showConfirm(Model model, @ModelAttribute("form") AccountRegisterForm form) {
        // 仕様変更（p.74 BP004）：手動リダイレクト記述（BP003へ戻すif文）を完全削除。
        model.addAttribute("form", form);
        return "admin/account/confirm";
    }

    /**
     * 2.5 確認画面からのポスト（登録ボタンまたは戻るボタン押下時）
     */
    @PostMapping("/confirm")
    public String handleConfirmAction(@ModelAttribute("form") AccountRegisterForm form,
            @RequestParam("action") String action,
            Model model,
            RedirectAttributes redirectAttributes) {

        // ［戻る］ボタン押下時は「redirect:」を完全に排除し、直接入力画面のHTMLを返却（フォワード）します。
        // これにより、HTMLに値のはめ込み属性が用意されている「社員選択」と「アカウント名」の2つは画面に復元されます。
        if ("back".equals(action)) {
            model.addAttribute("employees", accountRegisterService.getUnregisteredEmployees());
            model.addAttribute("form", form);
            return "admin/account/form";
        }

        if ("register".equals(action)) {
            try {
                if (accountRegisterService.isAccountNameDuplicate(form.getAccountName())) {
                    redirectAttributes.addFlashAttribute("errorMessages", List.of("このアカウント名は既に使用されています"));
                    model.addAttribute("employees", accountRegisterService.getUnregisteredEmployees());
                    model.addAttribute("form", form);
                    return "admin/account/form";
                }

                // データベースに登録を実行
                accountRegisterService.register(form);

                // 💡 PRG方式：登録が成功した「完成したformデータ」を、リダイレクトを跨げる FlashAttribute へ乗せます。
                // これにより、この直後にセッションをクリアしても、完了画面へ安全にデータが引き継がれます。
                redirectAttributes.addFlashAttribute("completedForm", form);

                return "redirect:/admin/account/complete";

            } catch (Exception e) {
                logger.error("登録処理に失敗しました。詳細エラー: ", e);
                // 仕様変更：エラー発生時、トップメニュー画面「BP001（/admin）」へリダイレクトします
                redirectAttributes.addFlashAttribute("errorMessages", List.of("登録処理に失敗しました。管理者に連絡してください"));
                return "redirect:/admin";
            }
        }

        model.addAttribute("employees", accountRegisterService.getUnregisteredEmployees());
        model.addAttribute("form", form);
        return "admin/account/form";
    }

    /**
     * 3. 担当者アカウント登録(完了)画面の表示 (BP005)
     */
    @GetMapping("/complete")
    public String showComplete(Model model,
            @ModelAttribute("form") AccountRegisterForm form,
            SessionStatus sessionStatus,
            RedirectAttributes redirectAttributes) {
        try {
            // 💡 完了画面が表示された最初の瞬間に、次の連続登録に備えて `@SessionAttributes` のセッションを綺麗にお掃除します。
            sessionStatus.setComplete();

            // 💡 PRG方式：リダイレクトの波に乗って届いた登録完了データ（completedForm）がModel内に存在する場合は、
            // 空っぽになってしまった通常の form をこの完了データで差し替えて画面（HTML）へ送ります。
            if (model.containsAttribute("completedForm")) {
                AccountRegisterForm completedForm = (AccountRegisterForm) model.asMap().get("completedForm");
                model.addAttribute("form", completedForm);
            } else {
                // 仕様変更（不正アクセス行の削除）に対応：
                // 2回目以降のリロードや、URL直接アクセスの場合、弾く記述は削除されているため、安全に表示だけを維持させます。
                model.addAttribute("form", form);
            }

            return "admin/account/complete";

        } catch (Exception e) {
            logger.error("完了画面の表示処理に失敗しました。詳細エラー: ", e);
            // 仕様変更（p.76 BP005 例外処理）：例外エラー発生時、トップ画面「BP001（/admin）」へ安全にリダイレクトします
            redirectAttributes.addFlashAttribute("errorMessages", List.of("データの取得に失敗しました。トップ画面に戻ります"));
            return "redirect:/admin";
        }
    }
}
