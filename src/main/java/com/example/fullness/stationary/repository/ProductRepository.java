package com.example.fullness.stationary.repository;

import com.example.fullness.stationary.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProductRepository {

    // ページング用の全件取得
    List<Product> findAllWithPaging(@Param("limit") int limit, @Param("offset") long offset);

    // 全件の総数カウント
    long countAll();

    // カテゴリIDで絞り込んだページング取得
    List<Product> findByCategoryIdWithPaging(@Param("categoryId") Integer categoryId, @Param("limit") int limit,
            @Param("offset") long offset);

    // カテゴリIDごとの総数カウント
    long countByCategoryId(@Param("categoryId") Integer categoryId);

}