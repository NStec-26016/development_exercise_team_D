package com.example.fullness.stationary.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.fullness.stationary.service.ProductService;
import com.example.fullness.stationary.entity.Product;

/**
 * 商品検索および商品情報メンテナンス画面の遷移と制御を行うコントローラークラス。
 * 
 * 本クラスは、カテゴリ別の商品検索、ページネーション処理、および
 * 登録・修正・削除画面への遷移制御を担当します。
 * 
 * @author Team_D
 * @version 1.0
 */
@Controller
public class ProductController {

    private final ProductService productService;

    /**
     * コンストラクタインジェクションにより ProductService を注入します。
     * 
     * @param productService 商品関連のビジネスロジックを提供するサービスクラス
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 商品検索画面（BP006）を表示します。
     * 
     * URL「/admin/product」に対するGETリクエストを処理します。
     * カテゴリマスタから全カテゴリを取得し、選択されたカテゴリに基づいて
     * 商品一覧をページング形式で取得します。
     * 
     * @param categoryId 検索対象のカテゴリID（指定なしの場合は全商品）
     * @param pageable   ページネーション情報（デフォルト：1ページ10件、商品ID昇順）
     * @param model      画面へデータを格納・送出するためのModelオブジェクト
     * @return 商品検索画面のビュー名 "admin/product/search"
     */
    @GetMapping("/admin/product")
    public String showProductList(
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @PageableDefault(size = 10, sort = "Id") Pageable pageable,
            Model model) {

        // ログイン情報の取得（共通ヘッダー制御用）
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {
            model.addAttribute("loginEmployeeName", authentication.getName());
            model.addAttribute("loggedIn", true);
        } else {
            model.addAttribute("loggedIn", false);
        }

        try {
            // 1. カテゴリ一覧を全件取得（プルダウン用：カテゴリID昇順）
            model.addAttribute("categories", productService.findAllCategories());

            // 2. 商品一覧を検索条件とページングで取得（商品ID昇順）
            Page<Product> productPage = productService.findProductsByCategory(categoryId, pageable);

            // 3. 検索結果・条件の格納
            // 3. 検索結果・条件の格納（HTML側が使っている変数名に合わせる）
            model.addAttribute("products", productPage.getContent()); // 商品リスト
            model.addAttribute("currentPage", productPage.getNumber() + 1); // 現在のページ番号（0始まりを1始まりに）
            model.addAttribute("totalPages", productPage.getTotalPages()); // 総ページ数
            model.addAttribute("selectedCategoryId", categoryId);

            // データが0件の場合のメッセージ制御
            if (productPage.getContent().isEmpty()) {
                model.addAttribute("message", "該当する商品情報がありません");
            }

        } catch (Exception e) {
            // 商品データ取得エラー時の処理
            model.addAttribute("errorMessage", "商品情報の取得に失敗しました");
            System.err.println("[ERROR] 商品情報取得中にエラーが発生しました。詳細: " + e.getMessage());
        }

        return "admin/product/search";
    }

}
