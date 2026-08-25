package com.example.fullness.stationary.service;

// JUnitの検証用メソッドをインポートします
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.entity.ProductCategory;

/**
 * 実際のデータベース（初期データ）と連携して
 * ProductServiceの動作を検証する統合テストクラス。
 */
@SpringBootTest
@Transactional // テストごとにデータベースの変更をロールバックします
class ProductServiceTest {

    // テスト対象となる実際の ProductService を自動注入（DI）します
    @Autowired
    private ProductService productService;

    @Test
    @DisplayName("findAllCategories: SQLの初期データから4つのカテゴリ（文房具、ガジェット、ファッション、日用品）が正しく取得できること")
    void testFindAllCategories_OK() {
        // サービスクラスの findAllCategories() を実行します
        List<ProductCategory> result = productService.findAllCategories();

        // 取得した結果が null でないことを検証します
        assertNotNull(result);
        // 初期データの通り、カテゴリの総件数が 4件 であることを検証します
        assertEquals(4, result.size());
        // 1番目のカテゴリ名が「文房具」であることを検証します
        assertEquals("文房具", result.get(0).getName());
        // 2番目のカテゴリ名が「ガジェット」であることを検証します
        assertEquals("ガジェット", result.get(1).getName());
        // 3番目のカテゴリ名が「ファッション」であることを検証します
        assertEquals("ファッション", result.get(2).getName());
        // 4番目のカテゴリ名が「日用品」であることを検証します
        assertEquals("日用品", result.get(3).getName());
    }

    @Test
    @DisplayName("findProductsByCategory: カテゴリIDが null の場合、初期データの全商品からページング付きで取得できること")
    void testFindProductsByCategory_NoCategory_OK() {
        // ページング条件（1ページ目、10件表示）を指定します
        Pageable pageable = PageRequest.of(0, 10);

        // カテゴリIDに null を指定してサービスクラスを実行します
        Page<Product> resultPage = productService.findProductsByCategory(null, pageable);

        // 取得したページ結果が null でないことを検証します
        assertNotNull(resultPage);
        // 1ページあたりの取得件数が指定通りの 10件 であることを検証します
        assertEquals(10, resultPage.getContent().size());
        // 初期データの全商品の総件数（21件）が正しく設定されていることを検証します
        assertEquals(21L, resultPage.getTotalElements());
    }

    @Test
    @DisplayName("findProductsByCategory: カテゴリID（1:文房具）が指定された場合、該当する商品がページング付きで取得できること")
    void testFindProductsByCategory_WithCategory_OK() {
        // 検索対象として初期データのカテゴリID「1（文房具）」を指定します
        Integer categoryId = 1;
        // ページング条件（1ページ目、10件表示）を指定します
        Pageable pageable = PageRequest.of(0, 10);

        // カテゴリIDを指定してサービスクラスを実行します
        Page<Product> resultPage = productService.findProductsByCategory(categoryId, pageable);

        // 取得したページ結果が null でないことを検証します
        assertNotNull(resultPage);
        // 1ページあたりの取得件数が指定通りの 10件 であることを検証します
        assertEquals(10, resultPage.getContent().size());
        // 初期データにおいて文房具に属する商品の総件数（14件）が正しく設定されていることを検証します
        assertEquals(14L, resultPage.getTotalElements());
    }
}