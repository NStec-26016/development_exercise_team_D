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

    List<Product> findByCategoryIdWithPaging(@Param("product_category_id") Integer product_category_id,
            @Param("limit") int limit,
            @Param("offset") long offset);

    long countByCategoryId(@Param("product_category_id") Integer product_category_id);

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
    void updateProduct(Product product);

}