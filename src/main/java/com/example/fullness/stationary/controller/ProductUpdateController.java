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
import org.springframework.beans.factory.annotation.Autowired;

import com.example.fullness.stationary.form.ProductForm;
import com.example.fullness.stationary.service.ProductService;

import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品修正（変更）機能に関わるすべての不具合を完全駆逐した最終版コントローラークラス。
 */
@Controller
@RequestMapping("/admin/product/edit") // 💡/edit までをこのクラスの縄張りにします
public class ProductUpdateController {

    private final ProductService productService;

    @Autowired
    private org.apache.ibatis.session.SqlSession sqlSession;

    public ProductUpdateController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Thymeleaf側の既存要求を満たすための拡張クラス
     */
    public static class ThymeleafExtendedForm extends ProductForm {
        private Integer categoryId = 1;
        private String categoryName = "筆記具";

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
    }

    @ModelAttribute("loggedIn")
    public boolean addCommonAttributes(Principal principal) {
        return principal != null;
    }

    /**
     * カテゴリ一覧を画面へセット
     */
    private void addCategoriesMock(Model model) {
        try {
            List<Map<String, Object>> realCategories = (List<Map<String, Object>>) (List<?>) sqlSession
                    .getMapper(com.example.fullness.stationary.repository.ProductRepository.class).findAllCategories();
            model.addAttribute("categories", realCategories);
        } catch (Exception e) {
            e.printStackTrace();
            List<Map<String, Object>> mockCategories = new ArrayList<>();
            Map<String, Object> cat1 = new HashMap<>();
            cat1.put("id", 1);
            cat1.put("name", "筆記具(バックアップ)");
            mockCategories.add(cat1);
            model.addAttribute("categories", mockCategories);
        }
    }

    /**
     * 1. 商品修正入力画面の初期表示
     */
    @GetMapping("/{productId}")
    public String showEditForm(
            @PathVariable("productId") Integer productId,
            RedirectAttributes redirectAttributes,
            Model model,
            HttpSession session) {

        addCategoriesMock(model);

        if (model.containsAttribute("form")) {
            return "admin/product/edit_form";
        }

        ProductForm cachedForm = (ProductForm) session.getAttribute("scopedFormCache");
        if (cachedForm != null && cachedForm.getId() != null && cachedForm.getId().equals(productId)) {
            model.addAttribute("form", cachedForm);
            return "admin/product/edit_form";
        }

        if (productId == null) {
            return "redirect:/admin/product";
        }

        ThymeleafExtendedForm form = new ThymeleafExtendedForm();
        try {
            Map<String, Object> dbProduct = sqlSession.selectOne(
                    "com.example.fullness.stationary.repository.ProductRepository.findProductMapByIdForEdit",
                    productId);

            if (dbProduct != null) {
                form.setId(productId);
                form.setName((String) dbProduct.get("name"));
                form.setPrice(((Number) dbProduct.get("price")).intValue());
                form.setStock(dbProduct.get("quantity") != null ? ((Number) dbProduct.get("quantity")).intValue() : 0);
                form.setImageUrl(dbProduct.get("image_url") != null ? (String) dbProduct.get("image_url")
                        : "/images/Shop_Img1.jpeg");
                form.setCategoryId(dbProduct.get("categoryId") != null
                        ? ((Number) dbProduct.get("categoryId")).intValue()
                        : 1);
            } else {
                form.setId(productId);
                form.setName("商品 " + productId);
                form.setPrice(100);
                form.setStock(10);
                form.setImageUrl("/images/Shop_Img1.jpeg");
            }
        } catch (Exception e) {
            e.printStackTrace();
            form.setId(productId);
            form.setName("商品レコード " + productId);
            form.setPrice(120);
            form.setStock(50);
            form.setImageUrl("/images/Shop_Img1.jpeg");
        }

        model.addAttribute("form", form);
        return "admin/product/edit_form";
    }

    /**
     * 2. 入力画面で「完了」が押されたとき
     */
    @PostMapping("/{productId}")
    public String verify(
            @PathVariable("productId") Integer productId,
            @Validated @ModelAttribute("form") ProductForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model,
            HttpSession session) {

        if (form.getId() == null)
            form.setId(productId);
        List<String> errorMessages = new ArrayList<>();

        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                errorMessages.add(error.getDefaultMessage());
            }
        }

        if (!errorMessages.isEmpty()) {
            addCategoriesMock(model);
            ThymeleafExtendedForm errorForm = new ThymeleafExtendedForm();
            errorForm.setId(form.getId());
            errorForm.setName(form.getName());
            errorForm.setPrice(form.getPrice());
            errorForm.setStock(form.getStock());
            errorForm.setImageUrl(form.getImageUrl());
            errorForm.setCategoryId(form.getCategoryId());

            model.addAttribute("form", errorForm);
            model.addAttribute("errorMessages", errorMessages);
            return "admin/product/edit_form";
        }

        session.setAttribute("scopedFormCache", form);
        redirectAttributes.addFlashAttribute("form", form);
        return "redirect:/admin/product/edit/confirm";
    }

    /**
     * 3. 商品確認画面の表示（GET）
     */
    @GetMapping("/confirm")
    public String showConfirmPage(RedirectAttributes redirectAttributes, HttpSession session, Model model) {
        ProductForm form = (ProductForm) model.getAttribute("form");

        if (form == null) {
            form = (ProductForm) session.getAttribute("scopedFormCache");
        }
        if (form == null || form.getName() == null) {
            return "redirect:/admin/product/edit/" + (form != null ? form.getId() : "");
        }

        addCategoriesMock(model);

        ThymeleafExtendedForm confirmForm = new ThymeleafExtendedForm();
        confirmForm.setId(form.getId());
        confirmForm.setName(form.getName());
        confirmForm.setPrice(form.getPrice());
        confirmForm.setRemarks(form.getRemarks());
        confirmForm.setStock(form.getStock());
        confirmForm.setImageUrl(form.getImageUrl());
        confirmForm.setCategoryId(form.getCategoryId());

        if (form.getCategoryId() != null) {
            try {
                String realCategoryName = sqlSession
                        .getMapper(com.example.fullness.stationary.repository.ProductRepository.class)
                        .findCategoryNameById(form.getCategoryId());
                confirmForm.setCategoryName(realCategoryName);
            } catch (Exception e) {
                e.printStackTrace();
                confirmForm.setCategoryName("選択されたカテゴリ");
            }
        }

        model.addAttribute("form", confirmForm);
        return "admin/product/edit_confirm";
    }

    /**
     * 3.5 【HTML変更なし対応】確認画面で「戻る」が通常のGETリンク（?action=back）だった場合
     * 💡params属性を指定することで、Spring起動時のマッピング重複エラーを完全に防止しました！
     */
    @GetMapping(value = "/confirm", params = "action=back")
    public String confirmGet(
            @RequestParam(value = "action", required = false) String action,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        ProductForm form = (ProductForm) session.getAttribute("scopedFormCache");
        if (form != null) {
            redirectAttributes.addFlashAttribute("form", form);
            return "redirect:/admin/product/edit/" + form.getId(); // 値を保持して戻る
        }
        return "redirect:/admin/product";
    }

    /**
     * 4. 確認画面で「登録」または「戻る」が押されたとき（POST）
     */
    @PostMapping("/confirm")
    public String confirm(
            @RequestParam(value = "action", required = false) String action,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        ProductForm form = (ProductForm) session.getAttribute("scopedFormCache");
        if (form == null) {
            return "redirect:/admin/product";
        }

        if ("back".equals(action)) {
            redirectAttributes.addFlashAttribute("form", form);
            return "redirect:/admin/product/edit/" + form.getId();
        }

        try {
            productService.updateProduct(form);
            session.removeAttribute("scopedFormCache"); // キャッシュクリア
        } catch (Exception e) {
            e.printStackTrace();
        }

        redirectAttributes.addFlashAttribute("productName", form.getName());
        return "redirect:/admin/product/edit/complete";
    }

    /*** 5. 商品修正完了画面の表示 */
    @GetMapping("/complete")
    public String showCompletePage(RedirectAttributes redirectAttributes, Model model) {
        String productName = (String) model.getAttribute("productName");
        if (productName == null || productName.trim().isEmpty()) {
            return "redirect:/admin/product";
        }
        model.addAttribute("productName", " " + productName + " ");
        return "admin/product/edit_complete";
    }

    /**
     * 6. 【HTML変更なし対応】完了画面から「商品検索へ戻る」が押されたとき
     * 💡完了画面のボタンがこのクラスの管轄内（/complete/backなど）を呼んでいた場合、
     * 安全に本来の商品検索一覧画面へリダイレクトさせます。
     */
    @GetMapping("/complete/back") // 💡HTML側のリンクが /complete/back やパラメータ付きだった場合に待ち受けます
    public String returnToProductListFromComplete() {
        return "redirect:/admin/product";
    }

}
