package com.example.fullness.stationary.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.entity.ProductCategory;

@Mapper
public interface ProductRepository {

    List<Product> findAllWithPaging(@Param("limit") int limit, @Param("offset") long offset);

    long countAll();

    List<Product> findByCategoryIdWithPaging(@Param("categoryId") Integer categoryId,
            @Param("limit") int limit,
            @Param("offset") long offset);

    long countByCategoryId(@Param("categoryId") Integer categoryId);

    // 全カテゴリを取得するメソッド
    List<ProductCategory> findAllCategories();

    // ↓↓↓ここからUC12
    /**
     * 商品名から商品情報を検索するメソッド（重複チェック用）
     * 
     * @param name 検索する商品名
     * @return 一致した商品エンティティ（存在しない場合はnull）
     */
    Product findByName(@Param("name") String name);

    /**
     * 商品情報を更新（修正）するメソッド
     * 
     * @param product 更新データを持つ商品エンティティ
     */
    // 既存のメソッドはそのまま残す
    void updateProduct(Product product);

    String findCategoryNameById(@Param("categoryId") Integer categoryId);

    // 💡【この1行を追加！】
    // Serviceを完全に迂回し、画像URL（imageUrl）が入ったFormデータを直接MyBatisに届ける新しい武器です！
    void updateProductDirectFromForm(@Param("form") com.example.fullness.stationary.form.ProductForm form);

}
