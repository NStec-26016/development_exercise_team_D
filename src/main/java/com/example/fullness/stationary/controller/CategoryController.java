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
 * @author 丸本
 * @version 1.0
 */
@Controller
@RequestMapping("admin/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 1. 商品カテゴリ登録入力画面(BP019)の初期表示
     */
    // 現在操作しているユーザーがログインしているかどうかをチェック
    @GetMapping("add")
    public String showAddForm(Principal principal, Model model) {
        model.addAttribute("loggedIn", principal != null);

        // 画面に渡すデータの中に form という名前のデータが存在しない場合だけ、新しく空っぽの CategoryForm
        // のインスタンス（データの受け皿となる箱）を作る
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new CategoryForm());
        }
        return "admin/category/form";
    }

    /**
     * 2. 入力画面(BP019)で「完了」が押されたとき（バリデーション＆重複チェック）
     */

    @PostMapping("add")
    public String verify(
            Principal principal,
            // 画面から送られてきた入力データを、自動的に form という箱（オブジェクト）に詰め込み、同時に必須チェックなどのバリデーションを実行します。
            @Validated @ModelAttribute("form") CategoryForm form,
            // バリデーションの結果（エラーが起きたかどうか）がここに自動で格納されます。
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        model.addAttribute("loggedIn", principal != null);

        List<String> errorMessages = new ArrayList<>();

        // 入力チェック（バリデーション）
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                errorMessages.add(error.getDefaultMessage());
            }
        }

        // 入力チェックに問題がない場合のみ、DBとの接続処理（重複チェック）を行う
        if (errorMessages.isEmpty()) {
            try {
                if (categoryService.isCategoryNameExists(form.getName())) {
                    errorMessages.add("入力されたカテゴリ名は既に登録されています。");
                }
            } catch (org.springframework.dao.DataAccessException e) {
                e.printStackTrace();
                errorMessages.add("システムエラーが発生しました。管理者に連絡してください");
            } catch (Exception e) {
                e.printStackTrace();
                errorMessages.add("システムエラーが発生しました。管理者に連絡してください");
            }
        }

        // 入力エラー、またはDB接続エラーが存在する場合は入力画面を再表示（URLは /add のまま）
        if (!errorMessages.isEmpty()) {
            model.addAttribute("errorMessages", errorMessages);
            return "admin/category/form";
        }

        // エラーがない場合、データを保持して確認画面へリダイレクト
        redirectAttributes.addFlashAttribute("form", form);
        return "redirect:/admin/category/add/confirm";
    }

    /**
     * 確認画面(BP020)をGETで表示する処理
     */
    @GetMapping("add/confirm")
    public String showConfirmPage(
            Principal principal,
            RedirectAttributes redirectAttributes,
            Model model) {

        model.addAttribute("loggedIn", principal != null);

        // リダイレクトで運ばれてきたformデータをModelから安全に取得する
        CategoryForm form = (CategoryForm) model.getAttribute("form");

        // URL直接入力やブラウザのリロード対策（フォームの中身が空、または文字が空なら入力画面へ戻す）
        if (form == null || form.getName() == null || form.getName().trim().isEmpty()) {
            List<String> errorMessages = new ArrayList<>();
            errorMessages.add("不正なアクセスです");
            redirectAttributes.addFlashAttribute("errorMessages", errorMessages);
            return "redirect:/admin/category/add";
        }

        // HTML側にフォームデータを明示的に引き渡す
        model.addAttribute("form", form);
        return "admin/category/confirm";
    }

    /**
     * 3. 確認画面(BP020)で「登録」または「戻る」が押されたとき
     */
    @PostMapping("add/confirm")
    public String confirm(
            Principal principal,
            @RequestParam("action") String action,
            @ModelAttribute("form") CategoryForm form,
            RedirectAttributes redirectAttributes,
            Model model) {

        model.addAttribute("loggedIn", principal != null);

        // 「戻る」が押された場合、入力データを保持して入力画面へリダイレクト
        if ("back".equals(action)) {
            redirectAttributes.addFlashAttribute("form", form);
            return "redirect:/admin/category/add";
        }

        if (form == null || form.getName() == null || form.getName().trim().isEmpty()) {
            List<String> errorMessages = new ArrayList<>();
            errorMessages.add("不正なアクセスです。");
            redirectAttributes.addFlashAttribute("errorMessages", errorMessages);
            return "redirect:/admin/category/add";
        }

        // 登録の直前にもう一度重複チェックとDBエラー対策を行う
        try {
            if (categoryService.isCategoryNameExists(form.getName())) {
                List<String> errorMessages = new ArrayList<>();
                errorMessages.add("入力されたカテゴリ名は既に登録されています");
                redirectAttributes.addFlashAttribute("form", form);
                redirectAttributes.addFlashAttribute("errorMessages", errorMessages);
                return "redirect:/admin/category/add";
            }

            // 重複がなければDB登録処理
            categoryService.registerCategory(form.getName());

        } catch (org.springframework.dao.DataAccessException e) {
            e.printStackTrace();
            List<String> errorMessages = new ArrayList<>();
            errorMessages.add("システムエラーが発生しました。管理者に連絡してください");
            redirectAttributes.addFlashAttribute("form", form);
            redirectAttributes.addFlashAttribute("errorMessages", errorMessages);
            return "redirect:/admin/category/add";
        } catch (Exception e) {
            e.printStackTrace();
            List<String> errorMessages = new ArrayList<>();
            errorMessages.add("システムエラーが発生しました。管理者に連絡してください");
            redirectAttributes.addFlashAttribute("form", form);
            redirectAttributes.addFlashAttribute("errorMessages", errorMessages);
            return "redirect:/admin/category/add";
        }

        redirectAttributes.addFlashAttribute("categoryName", form.getName());
        return "redirect:/admin/category/complete";
    }

    /**
     * 4. 登録完了画面(BP021)を表示する処理
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
     * 5. 例外処理（セッションデータ不足）の検知処理
     */
    @ExceptionHandler(HttpSessionRequiredException.class)
    public String handleSessionError(RedirectAttributes redirectAttributes) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add("不正なアクセスです");
        redirectAttributes.addFlashAttribute("errorMessages", errorMessages);
        return "redirect:/admin/category/add";
    }
}
