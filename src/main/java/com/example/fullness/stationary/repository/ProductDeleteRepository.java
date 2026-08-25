package com.example.fullness.stationary.repository;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.example.fullness.stationary.dto.ProductDetailDto;
import com.example.fullness.stationary.entity.Product;

import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductDeleteRepository {

    /**
     * 商品をIDで1件検索する
     */
    Product findById(@Param("productId") Integer productId);

    // 商品削除確認画面に表示する詳細情報を取得する
    ProductDetailDto findProductDetailById(@Param("productId") Integer id);

    // 商品をIDで1件削除する（実際にはdelete_flagを1にする）
    void deleteById(@Param("productId") Integer id);
}
