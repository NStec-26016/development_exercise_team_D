package com.example.fullness.stationary.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductStockRepository {
    /**
     * 指定された商品IDの在庫数を更新するメソッド
     */
    void updateStock(@Param("productId") Integer productId, @Param("stock") Integer stock);
}
