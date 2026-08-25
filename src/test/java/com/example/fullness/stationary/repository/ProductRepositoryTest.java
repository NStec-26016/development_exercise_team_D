package com.example.fullness.stationary.repository;

// JUnitの検証用メソッドをインポートします
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.entity.ProductCategory; // ProductCategoryに修正

/**
 * ProductRepositoryの単体テストクラス。
 */
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    // テスト対象となる ProductRepository を自動注入（DI）します
    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("findAllCategories: 4つのカテゴリ（文房具、ガジェット、ファッション、日用品）が正しく取得できること")
    void testFindAllCategories_OK() {
        // リポジトリの findAllCategories() を実行し、全カテゴリのリストを取得します
        List<ProductCategory> categories = productRepository.findAllCategories();

        // 取得したリストが null でないことを検証します
        assertNotNull(categories);
        // 取得したカテゴリの件数が 4件 であることを検証します
        assertEquals(4, categories.size());
        // 1番目のカテゴリ名が「文房具」であることを検証します
        assertEquals("文房具", categories.get(0).getName());
        // 2番目のカテゴリ名が「ガジェット」であることを検証します
        assertEquals("ガジェット", categories.get(1).getName());
        // 3番目のカテゴリ名が「ファッション」であることを検証します
        assertEquals("ファッション", categories.get(2).getName());
        // 4番目のカテゴリ名が「日用品」であることを検証します
        assertEquals("日用品", categories.get(3).getName());
    }

    @Test
    @DisplayName("findAllWithPaging: 全商品がページング付きで取得できること")
    void testFindAllWithPaging_OK() {
        // 1ページ10件、オフセット0を指定して、全商品のページング取得を実行します
        List<Product> products = productRepository.findAllWithPaging(10, 0L);

        // 取得したリストが null でないことを検証します
        assertNotNull(products);
        // 1ページあたりの取得件数が 10件 であることを検証します
        assertEquals(10, products.size());
    }

    @Test
    @DisplayName("countAll: 全商品の総数が21件であること")
    void testCountAll_OK() {
        // 全商品の総件数をカウントするメソッドを実行します
        long count = productRepository.countAll();

        // 取得した総件数が初期データ通りの 21L であることを検証します
        assertEquals(21L, count);
    }

    @Test
    @DisplayName("findByCategoryIdWithPaging: 指定したカテゴリID（1:文房具）に紐づく商品が取得できること")
    void testFindByCategoryIdWithPaging_OK() {
        // 検索条件としてカテゴリID「1（文房具）」を指定します
        Integer categoryId = 1;

        // 指定カテゴリの商品をページング（1ページ10件、オフセット0）で取得します
        List<Product> products = productRepository.findByCategoryIdWithPaging(categoryId, 10, 0L);

        // 取得したリストが null でないことを検証します
        assertNotNull(products);
        // 1ページあたりの取得件数が 10件 であることを検証します
        assertEquals(10, products.size());
    }

    @Test
    @DisplayName("countByCategoryId: 指定したカテゴリID（1:文房具）の件数が14件であること")
    void testCountByCategoryId_OK() {
        // 対象のカテゴリIDとして「1（文房具）」を指定します
        Integer categoryId = 1;

        // 指定したカテゴリに属する商品の総件数をカウントします
        long count = productRepository.countByCategoryId(categoryId);

        // カウント数が文房具の登録数である 14L であることを検証します
        assertEquals(14L, count);
    }
}