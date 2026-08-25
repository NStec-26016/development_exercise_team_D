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

    List<Product> findByCategoryIdWithPaging(@Param("categoryId") Integer categoryId, @Param("limit") int limit,
            @Param("offset") long offset);

    long countByCategoryId(@Param("categoryId") Integer categoryId);

    // 全カテゴリを取得するメソッド
    List<ProductCategory> findAllCategories();
}