package com.example.fullness.stationary.controller;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.form.ProductRegistrationForm;
import com.example.fullness.stationary.repository.ProductCategoryRepository;
import com.example.fullness.stationary.security.ProductRegistrationService;

@Controller
// 💡【共通URL】ご要望通り /admin/product/add をベースの階層にします
@RequestMapping("/admin/product/add")
@SessionAttributes("form")
public class ProductRegistrationController {

    @Autowired
    private ProductRegistrationService productService;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @ModelAttribute("form")
    public ProductRegistrationForm setUpForm() {
        ProductRegistrationForm form = new ProductRegistrationForm();
        form.setImagePath("/images/Shop_Img1.jpeg"); // 画像nullクラッシュ防止
        form.setCategoryName("");
        form.setName("");
        form.setCategoryId(0); // selected判定のnull安全対策
        return form;
    }

    /**
     * 1️⃣ ■ BP012 新商品登録(入力)画面を表示
     * ✨URL: GET /admin/product/add
     * クラスの共通URLの直下（空文字または "/"）にするため @GetMapping にしています
     */
    @GetMapping({ "", "/" })
    public String showAddForm(Model model) {
        model.addAttribute("errorMessages", null); // 通常時はピンクのエラー枠を完全に消す

        // 💡 共通レイアウト(layout.html)の th:if="${loggedIn}" を反応させる
        model.addAttribute("loggedIn", true); // 👈【追加】

        // データベースから本物のカテゴリ一覧を取得してHTMLに引き渡す
        List<ProductCategory> categoryList = productCategoryRepository.findAllByOrderByCategoryIdAsc();
        model.addAttribute("categories", categoryList);

        return "admin/product/add_form";
    }

    /**
     * 2️⃣ ■ BP013 新商品登録(確認)画面へのデータ処理
     * ✨URL: POST /admin/product/add
     * 入力画面のフォームからデータを受け取り、エラーがなければ確認画面URLへ「リダイレクト」します
     */
    @PostMapping({ "", "/" })
    public String confirmProduct(@Validated @ModelAttribute("form") ProductRegistrationForm form,
            BindingResult result, Model model) {

        // 📸 【ここを修正しました！】画像のバリデーションチェック（必須を解除）
        MultipartFile file = form.getImage();
        // 💡 画像がアップロードされている場合のみ、形式のチェックを行います（空ならスルー）
        if (file != null && !file.isEmpty()) {
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png")
                    && !contentType.equals("image/webp"))) {
                result.rejectValue("image", "invalidFormat", "正しい画像形式でアップロードしてください");
            } else {
                // 💡【追加行①】確認画面の th:src に渡すために、選択されたファイル名（例: bag.jpg）をセットします
                form.setImagePath("/images/" + file.getOriginalFilename());
            }
        }

        // ⚠️ バリデーションエラー、または型変換エラーがある場合の処理
        if (result.hasErrors()) {
            List<String> pureJapaneseMessages = result.getFieldErrors().stream()
                    .map(error -> {
                        String field = error.getField();
                        String code = error.getCode();

                        // 「aaaaa」の文字や「5000000...」の桁あふれ（型変換エラー）の条件
                        if ("typeMismatch".equals(code)) {
                            if ("price".equals(field)) {
                                return "正しい価格形式で入力してください";
                            }
                            if ("stock".equals(field)) {
                                return "正しい在庫数形式で入力してください";
                            }
                            return "正しい数値形式で入力してください";
                        }

                        // 100万以上（@Max）や未入力（@NotBlank）などはFormに記入したメッセージをそのまま使う
                        return error.getDefaultMessage();
                    })
                    .distinct() // メッセージの重複を綺麗に排除する
                    .collect(Collectors.toList());

            // グローバルエラー（手動追加した画像エラーなど）をリストに合流
            result.getGlobalErrors().forEach(error -> pureJapaneseMessages.add(error.getDefaultMessage()));

            model.addAttribute("errorMessages", pureJapaneseMessages);
            model.addAttribute("categories", productCategoryRepository.findAllByOrderByCategoryIdAsc());
            return "admin/product/add_form";
        }

        // 選択されたcategoryIdに対応する「本物のカテゴリ名」をDBから探して確認画面に引き継ぐ
        if (form.getCategoryId() != null) {
            List<ProductCategory> categories = productCategoryRepository.findAllByOrderByCategoryIdAsc();
            for (ProductCategory cat : categories) {
                if (cat.getId().equals(form.getCategoryId())) {
                    form.setCategoryName(cat.getName());
                    break;
                }
            }
        }

        // ⭕ ご要望の確認画面のURL（/admin/product/add/confirm）へリダイレクトして遷移させます
        return "redirect:/admin/product/add/confirm";
    }

    /**
     * 3️⃣ ■ BP013 新商品登録(確認)画面を表示
     * ✨URL: GET /admin/product/add/confirm
     */
    @GetMapping("/confirm")
    public String showConfirmPage(@ModelAttribute("form") ProductRegistrationForm form, Model model) {
        model.addAttribute("loggedIn", true); // 👈【ここを追加】
        return "admin/product/add_confirm";
    }

    /**
     * 4️⃣ 確定処理：戻る or 完了
     * ✨URL: POST /admin/product/add/confirm
     */
    @PostMapping("/confirm")
    public String handleConfirmAction(@ModelAttribute("form") ProductRegistrationForm form,
            @RequestParam(value = "action", required = false) String action,
            SessionStatus sessionStatus, RedirectAttributes redirectAttributes) {
        if ("back".equals(action)) {
            // 戻るボタンの際はリダイレクトして、入力画面（GET /admin/product/add）を綺麗に通します
            return "redirect:/admin/product/add";
        }

        // 💡【追加行②】完了ボタンを押したとき、選択されたファイルをチームDのimagesフォルダに物理保存します
        MultipartFile file = form.getImage();
        if (file != null && !file.isEmpty()) {
            try {
                // 💡【追加行③】指定のフォルダパス（C:\Users\...\images\）にファイルを出力します
                file.transferTo(new java.io.File(
                        "C:\\Users\\fullness\\development_exercise_team_D\\src\\main\\resources\\static\\images\\"
                                + file.getOriginalFilename()));
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }

        productService.registerProduct(form);
        redirectAttributes.addFlashAttribute("productName", form.getName());
        sessionStatus.setComplete();
        // ⭕ ご要望の完了画面のURL（/admin/product/add/complete）へリダイレクトします
        return "redirect:/admin/product/add/complete";
    }

    /**
     * 5️⃣ ■ BP014 新商品登録(完了)画面を表示
     * ✨URL: GET /admin/product/add/complete
     */
    @GetMapping("/complete")
    public String showCompletePage(Model model) {
        model.addAttribute("loggedIn", true); // 👈【ここを追加】
        return "admin/product/add_complete";
    }
}
