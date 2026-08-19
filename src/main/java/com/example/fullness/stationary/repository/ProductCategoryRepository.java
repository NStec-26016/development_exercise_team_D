package com.example.fullness.stationary.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ProductCategoryRepository {

    /**
     * 商品カテゴリを登録するメソッド
     * 💡 SQL文はXMLファイル側に記述するため、ここではメソッドの定義のみを行います。
     * 
     * @param name 登録するカテゴリ名
     */
    void insertCategory(@Param("name") String name);

}
