package com.example.fullness.stationary.repository;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.example.fullness.stationary.dto.ProductDetailDto;
import com.example.fullness.stationary.entity.Product;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.fullness.stationary.entity.Product;

@Mapper
public interface ProductRepository {

    // 全件取得（ページング用）
    List<Product> findAllWithPaging(@Param("limit") int limit, @Param("offset") long offset);

    // カテゴリ別取得（ページング用）
    List<Product> findByCategoryIdWithPaging(@Param("categoryId") Integer categoryId, @Param("limit") int limit,
            @Param("offset") long offset);

    // 全件数カウント
    long countAll();

    // カテゴリ別件数カウント
    long countByCategoryId(@Param("categoryId") Integer categoryId);

    // 商品削除確認画面に表示する詳細情報を取得する
    ProductDetailDto findProductDetailById(@Param("productId") Integer id);

    // 商品をIDで1件削除する（実際にはdelete_flagを1にする）
    void deleteById(@Param("productId") Integer id);
}
