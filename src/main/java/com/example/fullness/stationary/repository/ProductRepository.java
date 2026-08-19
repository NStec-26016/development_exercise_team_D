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
