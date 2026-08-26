package com.example.fullness.stationary.repository;

import org.apache.ibatis.annotations.Mapper;
import com.example.fullness.stationary.entity.Product;

@Mapper
public interface ProductRegistrationRepository {

    /**
     * 新しい商品を登録します。
     * 
     * @param product 登録する商品エンティティ
     * @return 登録に成功した件数
     */
    int insertProductRegistration(Product product);

}