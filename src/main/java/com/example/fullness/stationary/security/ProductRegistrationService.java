package com.example.fullness.stationary.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.form.ProductRegistrationForm;
import com.example.fullness.stationary.repository.ProductRepository;

@Service
public class ProductRegistrationService {
    @Autowired
    ProductRepository productRepository;

    /**
     * 新しい商品をデータベースに登録します。
     * 
     * @param form 画面から入力された商品情報（Formオブジェクト）
     */
    @Transactional
    public void registerProduct(ProductRegistrationForm form) {
        // 1. DB保存用のEntityオブジェクトを生成
        Product product = new Product();

        // 2. Formから取得したデータをEntityに詰め替える
        product.setProduct_category_id(form.getCategoryId());
        product.setName(form.getName());
        product.setPrice(form.getPrice());
        product.setImage_url(form.getImagePath());

        // 3. 新規登録時は削除フラグを「0（未削除・有効）」に設定
        product.setDelete_flag(0);

        // 4. Repository（MyBatis）を呼び出してDBにインサートを実行
        productRepository.insertProduct(product);
    }
}
