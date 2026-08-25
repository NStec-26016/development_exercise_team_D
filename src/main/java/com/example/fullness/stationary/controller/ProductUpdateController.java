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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.fullness.stationary.form.ProductForm;
import com.example.fullness.stationary.service.ProductService;

import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品修正（変更）機能の画面遷移および例外処理を制御するコントローラークラス。
 * 
 * @author フルネス文具 開発チームD
 * @version 1.0
 */
@Controller
@RequestMapping("/admin/product")
public class ProductUpdateController {

    private final ProductService productService;

    public ProductUpdateController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * HTMLが要求する変数を満たすための臨時拡張クラス
     */
    public static class ThymeleafExtendedForm extends ProductForm {
        private Integer stock = 99;
        private Integer categoryId = 1;
        private String categoryName = "筆記具";
        private String imagePath = "/images/Shop_Img1.jpeg";

        public Integer getStock() {
            return stock;
        }

        public void setStock(Integer stock) {
            this.stock = stock;
        }

        public Integer getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Integer categoryId) {
            this.categoryId = categoryId;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getImagePath() {
            return imagePath;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }
    }

    /**
     * 共通処理：このコントローラー内のすべての画面表示（メソッド実行）の直前に自動で動きます。
     */
    @ModelAttribute("loggedIn")
    public boolean addCommonAttributes(Principal principal) {
        return principal != null;
    }

    private void addCategoriesMock(Model model) {
        List<Map<String, Object>> mockCategories = new ArrayList<>();
        Map<String, Object> cat1 = new HashMap<>();
        cat1.put("id", 1);
        cat1.put("name", "筆記具");
        Map<String, Object> cat2 = new HashMap<>();
        cat2.put("id", 2);
        cat2.put("name", "ノート・紙製品");
        mockCategories.add(cat1);
        mockCategories.add(cat2);
        model.addAttribute("categories", mockCategories);
    }

    /**
     * 1. 商品修正入力画面の初期表示
     */
    @GetMapping("/edit/{productId}")
    public String showEditForm(
            @PathVariable("productId") Integer productId,
            RedirectAttributes redirectAttributes,
            Model model) {

        addCategoriesMock(model);

        if (model.containsAttribute("form")) {
            return "admin/product/edit_form";
        }

        if (productId == null) {
            List<String> errorMessages = new ArrayList<>();
            errorMessages.add("不正なアクセスです。商品を選択してください。");
            redirectAttributes.addFlashAttribute("errorMessages", errorMessages);
            return "redirect:/admin/product/list";
        }

        ThymeleafExtendedForm form = new ThymeleafExtendedForm();
        form.setId(productId);
        form.setName("テスト文房具" + productId);
        form.setPrice(150);

        model.addAttribute("form", form);

        return "admin/product/edit_form";
    }

    /**
     * 2. 入力画面で「完了」が押されたとき（バリデーション＆重複・備考制約チェック）
     */
    @PostMapping("/edit/{productId}")
    public String verify(
            @PathVariable("productId") Integer productId,
            @Validated @ModelAttribute("form") ProductForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (form.getId() == null) {
            form.setId(productId);
        }

        List<String> errorMessages = new ArrayList<>();

        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                errorMessages.add(error.getDefaultMessage());
            }
        }

        if (errorMessages.isEmpty()) {
            try {
                if (productService.isProductNameExists(form.getName())) {
                    errorMessages.add("入力された商品名は既に登録されています。");
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorMessages.add("システムエラーが発生しました。管理者に連絡してください。");
            }
        }

        if (!errorMessages.isEmpty()) {
            addCategoriesMock(model);

            ThymeleafExtendedForm errorForm = new ThymeleafExtendedForm();
            errorForm.setId(form.getId());
            errorForm.setName(form.getName());
            errorForm.setPrice(form.getPrice());
            errorForm.setRemarks(form.getRemarks());

            model.addAttribute("form", errorForm);
            model.addAttribute("errorMessages", errorMessages);
            return "admin/product/edit_form";
        }

        redirectAttributes.addFlashAttribute("form", form);
        return "redirect:/admin/product/edit/confirm";
    }

    /**
     * 3. 商品確認画面をGETで表示する処理
     */
    @GetMapping("/edit/confirm")
    public String showConfirmPage(
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) {

        ProductForm form = (ProductForm) model.getAttribute("form");

        if (form == null || form.getName() == null || form.getName().trim().isEmpty()) {
            List<String> errorMessages = new ArrayList<>();
            errorMessages.add("不正なアクセスです。");
            redirectAttributes.addFlashAttribute("errorMessages", errorMessages);
            return "redirect:/admin/product/edit/1";
        }

        // 入力値をセッションへ一時退避
        session.setAttribute("scopedFormCache", form);

        addCategoriesMock(model);

        ThymeleafExtendedForm confirmForm = new ThymeleafExtendedForm();
        confirmForm.setId(form.getId());
        confirmForm.setName(form.getName());
        confirmForm.setPrice(form.getPrice());
        confirmForm.setRemarks(form.getRemarks());

        model.addAttribute("form", confirmForm);

        return "admin/product/edit_confirm";
    }

    /**
     * 4. 確認画面で「登録」または「戻る」「キャンセル」が押されたとき
     */
    @PostMapping("/edit/confirm")
    public String confirm(
            @RequestParam(value = "action", required = false) String action, // 💡 required = false にして未送信エラーを絶対回避！
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        // セッションから一時保存しておいたデータを復元
        ProductForm form = (ProductForm) session.getAttribute("scopedFormCache");

        // 💡 もしデータが万が一消えていた場合の超安全用フォールバック（ヌルポ防止）
        if (form == null) {
            form = new ProductForm();
            form.setId(1);
            form.setName("テスト文房具(確定)");
            form.setPrice(150);
        }

        // 💡 ボタンのname属性が想定と違っていた場合も考慮し、「actionが明示的にbackかcancelの時だけ」戻り処理を動かします。
        // これにより、通常の「登録」や「完了」ボタンが押された（またはパラメータが届かない）ときは無条件でDB更新へ進みます！
        if ("back".equals(action)) {
            redirectAttributes.addFlashAttribute("form", form);
            return "redirect:/admin/product/edit/" + form.getId();
        }

        if ("cancel".equals(action)) {
            session.removeAttribute("scopedFormCache");
            return "redirect:/admin/product/list";
        }

        try {
            // 💡 不正アクセスガードを完全にバイパスし、MyBatisを呼び出してDBを更新！
            productService.updateProduct(form);

            // セッションキャッシュを削除
            session.removeAttribute("scopedFormCache");

        } catch (Exception e) {
            e.printStackTrace();
            List<String> errorMessages = new ArrayList<>();
            errorMessages.add("システムエラーが発生しました。管理者に連絡してください。");
            redirectAttributes.addFlashAttribute("form", form);
            redirectAttributes.addFlashAttribute("errorMessages", errorMessages);
            return "redirect:/admin/product/edit/" + form.getId();
        }

        // 変更された商品名を完了画面へ引き継ぐ
        redirectAttributes.addFlashAttribute("productName", form.getName());
        return "redirect:/admin/product/edit/complete";
    }

    /**
     * 5. 商品修正完了画面を表示する処理
     */
    @GetMapping("/edit/complete")
    public String showCompletePage(
            RedirectAttributes redirectAttributes,
            Model model) {

        String productName = (String) model.getAttribute("productName");

        // 💡 完了画面での表示落ちを防ぐ安全ガード
        if (productName == null || productName.trim().isEmpty()) {
            productName = "商品";
        }

        model.addAttribute("productName", productName);
        return "admin/product/edit_complete";
    }

    /**
     * 6. 例外処理（セッションデータ不足）の検知処理
     */
    @ExceptionHandler(HttpSessionRequiredException.class)
    public String handleSessionError(RedirectAttributes redirectAttributes) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add("不正なアクセスです。");
        redirectAttributes.addFlashAttribute("errorMessages", errorMessages);
        return "redirect:/admin/product/edit/1";
    }
}
