package com.example.fullness.stationary.repository;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.example.fullness.stationary.entity.Product;

@Mapper
@Repository
public interface ProductRepository {

    /**
     * 新しい商品を登録します。
     * 
     * @param product 登録する商品エンティティ
     * @return 登録に成功した件数（通常は1件成功なら 1 が返ります）
     */
    int insertProduct(Product product);

}
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
