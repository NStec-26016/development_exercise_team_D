package com.example.fullness.stationary.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductStockRepository {
    /**
     * 💡【バグ修正】引数名を XML のプレースホルダー（#{productId}, #{stock}）と完全に一致させます。
     * 
     * @Param を明示的につけることで、MyBatisが迷わず quantity カラムを更新できるようになります！
     */
    void updateStock(@Param("productId") Integer productId, @Param("stock") Integer stock);
}
