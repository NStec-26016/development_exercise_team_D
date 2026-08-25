package com.example.fullness.stationary.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.form.ProductRegistrationForm;
import com.example.fullness.stationary.repository.ProductRegistrationRepository;

@Service
public class ProductRegistrationService {

    @Autowired
    private ProductRegistrationRepository productRegistrationRepository;

    /**
     * 新しい商品をデータベースに登録します。
     * 
     * @param form 画面から入力された商品情報（Formオブジェクト）
     */
    @Transactional
    public void registerProduct(ProductRegistrationForm form) {

        Product product = new Product();

        product.setProduct_category_id(form.getCategoryId());

        product.setName(form.getName());
        product.setPrice(form.getPrice());

        product.setImageUrl(form.getImageUrl());

        product.setStock(form.getStock());

        product.setDelete_flag(0);

        productRegistrationRepository.insertProductRegistration(product);
    }
}