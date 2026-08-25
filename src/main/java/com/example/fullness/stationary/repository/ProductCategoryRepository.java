package com.example.fullness.stationary.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.example.fullness.stationary.entity.ProductCategory;

@Mapper
@Repository
public interface ProductCategoryRepository {

    /**
     * 「商品カテゴリを登録するメソッド」「既に登録されているカテゴリーを検索するメソッド」を定義する。
     * SQL文はXMLファイル側に記述するため、メソッドの定義のみを行う。
     * 
     * @param name 登録するカテゴリ名
     */
    void insertCategory(@Param("name") String name);

    ProductCategory findByName(@Param("name") String name);

    List<ProductCategory> findAllByOrderByCategoryIdAsc();
}
