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

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.service.ProductService;

/**
 * 商品検索および商品情報メンテナンス画面の遷移と制御を行うコントローラークラス。
 * 
 * @author Team_D
 * @version 1.0
 */
@Controller
public class ProductController {

    // 1ページの最大表示件数を定義する定数
    private static final int DEFAULT_PAGE_SIZE = 10;
    
    // 商品検索画面のビュー（HTMLテンプレート）のパスを定義する定数
    private static final String VIEW_PRODUCT_SEARCH = "admin/product/search";
    
    // 商品追加画面のビュー（HTMLテンプレート）のパスを定義する定数
    private static final String VIEW_PRODUCT_ADD_FORM = "admin/product/add_form";

    // ビジネスロジックを呼び出すためのサービスクラスのフィールド
    private final ProductService productService;

    /**
     * コンストラクタインジェクションにより ProductService を注入します。
     */
    public ProductController(ProductService productService) {
        // 受け取ったProductServiceのインスタンスをフィールドに保持する
        this.productService = productService;
    }

    /**
     * 商品検索画面（BP006）を表示します。
     */
    @GetMapping("/admin/product")
    public String showProductList(
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "id") Pageable pageable,
            Model model) {

        // ログインユーザーの情報を画面に渡すため、共通メソッドを呼び出す
        setLoginUserInfo(model);

        try {
            // プルダウンメニュー用として、サービス経由ですべてのカテゴリ一覧を取得してModelに格納する
            model.addAttribute("categories", productService.findAllCategories());

            // 検索条件（カテゴリID）とページング情報を基に、サービスから対象商品のPageオブジェクトを取得する
            Page<Product> productPage = productService.findProductsByCategory(categoryId, pageable);

            // 取得したPageオブジェクトから商品データのリストを取り出し、Modelに格納する
            model.addAttribute("products", productPage.getContent());
            
            // ページネーション用として、現在のページ番号（0始まりを画面用の1始まりに変換）をModelに格納する
            model.addAttribute("currentPage", productPage.getNumber() + 1);
            
            // ページネーション用として、全ページ数をModelに格納する
            model.addAttribute("totalPages", productPage.getTotalPages());
            
            // 画面側で選択中のカテゴリを維持できるように、検索に使ったカテゴリIDをModelに格納する
            model.addAttribute("selectedCategoryId", categoryId);

            // 取得した商品リストが空（0件）であるかどうかを判定する
            if (productPage.getContent().isEmpty()) {
                // 0件の場合は、画面に表示するメッセージをModelに格納する
                model.addAttribute("message", "該当する商品情報がありません");
            }

        } catch (Exception e) {
            // 予期せぬ例外が発生した場合、エラーメッセージをModelに格納する
            model.addAttribute("errorMessage", "商品情報の取得に失敗しました");
            
            // デバッグ用として、標準エラー出力にエラーの詳細情報を出力する
            System.err.println("[ERROR] 商品情報取得中にエラーが発生しました。詳細: " + e.getMessage());
        }

        // 処理が成功・失敗したいずれの場合も、商品検索画面のビュー名を返却して画面を表示する
        return VIEW_PRODUCT_SEARCH;
    }

    /**
     * 商品追加画面を表示します。
     */
    @GetMapping("/admin/product/add")
    public String showAddForm(Model model) {
        // 新規登録フォームと紐付けるための、空のProductインスタンスをModelに格納する
        model.addAttribute("form", new Product());

        // 追加画面のカテゴリ選択プルダウン用として、すべてのカテゴリ一覧をModelに格納する
        model.addAttribute("categories", productService.findAllCategories());

        // 商品追加画面のビュー名を返却してフォーム画面を表示する
        return VIEW_PRODUCT_ADD_FORM;
    }

    /**
     * 現在のログインユーザー情報を取得し、Modelに設定するプライベートメソッド。
     */
    private void setLoginUserInfo(Model model) {
        // Spring Securityのコンテキストから現在の認証情報を取得する
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // 認証情報が存在し、かつ未認証（anonymousUser）ではないかをチェックする
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {
            
            // ログインしている社員の名前をModelに格納する
            model.addAttribute("loginEmployeeName", authentication.getName());
            
            // ログイン状態であること（true）をModelに格納する
            model.addAttribute("loggedIn", true);
        } else {
            // 未ログイン状態であること（false）をModelに格納する
            model.addAttribute("loggedIn", false);
        }
    }
}