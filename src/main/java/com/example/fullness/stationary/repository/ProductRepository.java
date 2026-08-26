package com.example.fullness.stationary.repository;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

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
}
