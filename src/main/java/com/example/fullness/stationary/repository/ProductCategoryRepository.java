package com.example.fullness.stationary.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import com.example.fullness.stationary.entity.ProductCategory;

@Mapper
public interface ProductCategoryRepository {
    List<ProductCategory> findAllByOrderByCategoryIdAsc();

    // カテゴリIDを元に、特定のカテゴリ名（文字列）を1件取得する
    // （商品削除確認画面でカテゴリ名を表示するために使用）
    String findNameByCategoryId(@Param("categoryId") Integer categoryId);
}