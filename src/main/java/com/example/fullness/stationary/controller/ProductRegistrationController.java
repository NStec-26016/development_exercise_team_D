package com.example.fullness.stationary.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.fullness.stationary.form.ProductRegistrationForm;
import com.example.fullness.stationary.security.ProductRegistrationService;

@Controller
@RequestMapping("/admin/product")
@SessionAttributes("form")
public class ProductRegistrationController {

    @Autowired
    private ProductRegistrationService productService;

    @ModelAttribute("form")
    public ProductRegistrationForm setUpForm() {
        return new ProductRegistrationForm();
    }

    /** 1️⃣ 入力画面表示 (GET /admin/product/add) */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        // 🛠️【修正：ピンクの枠を消す】通常時は null を渡すことで、HTML側の th:if が非表示（消去）と判定してくれます。
        model.addAttribute("errorMessages", null);
        model.addAttribute("categories", new ArrayList<>());
        return "admin/product/add_form";
    }

    /** 2️⃣ 確認画面へ遷移 (POST /admin/product/add) */
    @PostMapping("/add")
    public String confirmProduct(@Validated @ModelAttribute("form") ProductRegistrationForm form,
            BindingResult result, Model model) {

        if (result.hasErrors()) {
            // 🛠️【修正：とんでもないエラー文を日本語だけにする】
            // 生のエラーオブジェクトではなく、あなたがFormクラスに指定した日本語の「メッセージ内容（String）」だけを綺麗に抜き出します。
            List<String> pureJapaneseMessages = result.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());

            // 綺麗な日本語リストだけを画面に渡す（これでピンクの枠が出現し、綺麗な日本語だけが並びます）
            model.addAttribute("errorMessages", pureJapaneseMessages);
            model.addAttribute("categories", new ArrayList<>());
            return "admin/product/add_form";
        }

        // 表示用の項目を一時補完（確認画面クラッシュ防止）
        form.setCategoryName("文房具");
        form.setImagePath("/images/Shop_Img1.jpeg");
        return "admin/product/add_confirm";
    }

    /** 3️⃣ 確定処理：戻る or 完了 (POST /admin/product/add/confirm) */
    @PostMapping("/add/confirm")
    public String handleConfirmAction(@ModelAttribute("form") ProductRegistrationForm form,
            @RequestParam(value = "action", required = false) String action,
            SessionStatus sessionStatus, RedirectAttributes redirectAttributes) {
        if ("back".equals(action)) {
            return "admin/product/add_form";
        }

        productService.registerProduct(form);
        redirectAttributes.addFlashAttribute("productName", form.getName());
        sessionStatus.setComplete();
        return "redirect:/admin/product/complete";
    }

    /** 4️⃣ 完了画面表示 (GET /admin/product/complete) */
    @GetMapping("/complete")
    public String showCompletePage() {
        return "admin/product/add_complete";
    }
}
