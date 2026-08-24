package com.example.fullness.stationary.repository;

// 💡JUnit 5 のインポート文
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.fullness.stationary.entity.ProductCategory;

/**
 * {@link ProductCategoryRepository} のユニットテストクラス。
 * 
 * アプリ全体を起動して実際のデータベース処理を検証します。
 * テスト終了後は自動的にロールバックされるため、本番データを汚しません。
 */
@SpringBootTest // 💡アプリ全体を起動してテストする設定
@Transactional // 💡超重要：テスト終了後に自動でロールバック（データを消去）する設定
public class ProductCategoryRepositoryTest {

    @Autowired
    private ProductCategoryRepository repository; // 💡テスト対象の本物のリポジトリを注入

    @Test
    @DisplayName("新しい商品カテゴリ名がデータベースに正しく登録・検索できること")
    public void testInsertAndFindCategory_OK() {
        // 1. 準備：テスト用のユニークなカテゴリ名を用意
        String testCategoryName = "テスト用文房具_SBT";

        // 2. 実行：リポジトリのinsertCategoryメソッドを呼び出して登録
        repository.insertCategory(testCategoryName);

        // 3. 検証：登録した名前で検索(findByName)をかけ、データが取得できるか確かめる
        ProductCategory actualCategory = repository.findByName(testCategoryName);

        // 💡検証（アサーション）の部分
        // 結果がnullでない（1件データが取得できている）こと
        assertNotNull(actualCategory);

        // 第一引数に「期待する値（testCategoryName）」、第二引数に「実際の値」を指定して一致すること
        assertEquals(testCategoryName, actualCategory.getName());
    }
}
