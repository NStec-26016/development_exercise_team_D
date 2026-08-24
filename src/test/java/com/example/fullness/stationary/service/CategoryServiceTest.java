package com.example.fullness.stationary.service;

// 💡追加：JUnit 5 の assertTrue インポート文
import static org.junit.jupiter.api.Assertions.assertTrue;
// 💡追加：Mockitoのwhen インポート文
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.repository.ProductCategoryRepository;

/**
 * {@link CategoryService} のユニットテストクラス。
 * 
 * Mockを使用し、データベースに接続することなく、
 * サービスがリポジトリを正しく呼び出せているかを高速に検証します。
 */
@ExtendWith(MockitoExtension.class) // モック機能を有効化
public class CategoryServiceTest {

    @Mock
    private ProductCategoryRepository productCategoryRepository; // リポジトリの偽物（モック）を作成

    @InjectMocks
    private CategoryService categoryService; // モックを自動で注入したテスト対象のサービス

    @Test
    @DisplayName("registerCategoryメソッドが、リポジトリのinsertCategoryを正しく呼び出すこと")
    public void testRegisterCategory_OK() {
        // 1. 準備：テスト用のカテゴリ名を用意
        String testCategoryName = "テスト用文房具";

        // 2. 実行：テスト対象のサービスメソッドを呼び出す
        categoryService.registerCategory(testCategoryName);

        // 3. 検証：モック化したリポジトリの insertCategory メソッドが「引数に指定の文字列を持って」「ちょうど1回」呼ばれたかをチェックする
        verify(productCategoryRepository, times(1)).insertCategory(testCategoryName);
    }

    @Test
    @DisplayName("isCategoryNameExists：DBに同じカテゴリ名が既に存在する場合、trueを返すこと")
    public void testIsCategoryNameExists_OK() {
        // 1. 準備：テストデータと、DBからデータが見つかるダミーのエンティティを用意
        String testName = "テスト用シャープペンシル";
        ProductCategory dummyCategory = new ProductCategory();
        dummyCategory.setName(testName);

        // モック設定：「repository.findByName("テスト用シャープペンシル") が呼ばれたら、データ（dummyCategory）を返す」
        when(productCategoryRepository.findByName(testName)).thenReturn(dummyCategory);

        // 2. 実行：テスト対象のサービスメソッドを呼び出す
        boolean result = categoryService.isCategoryNameExists(testName);

        // 3. 検証：結果が true であることを確認
        assertTrue(result);
    }
}
