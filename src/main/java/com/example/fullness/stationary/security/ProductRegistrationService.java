package com.example.fullness.stationary.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.form.ProductRegistrationForm;
import com.example.fullness.stationary.repository.ProductRegistrationRepository; // ★新しいレポジトリを使用

@Service
public class ProductRegistrationService {

    @Autowired
    private ProductRegistrationRepository productRegistrationRepository; // 🛠️ 新しい名前に完全対応

    /**
     * 新しい商品をデータベースに登録します。
     * 
     * @param form 画面から入力された商品情報（Formオブジェクト）
     */
    @Transactional
    public void registerProduct(ProductRegistrationForm form) {
        // 1. DB保存用のEntityオブジェクトを生成
        Product product = new Product();

        // 2. Formから取得したデータをEntityに詰め替える（エラーが完全に消滅します）
        // 🛠️【修正】エンティティの定義（product_category_id）に完全に合わせました
        product.setProduct_category_id(form.getCategoryId());

        product.setName(form.getName());
        product.setPrice(form.getPrice());

        // 🛠️【修正】エンティティの定義（imageUrl）に完全に合わせました
        product.setImageUrl(form.getImagePath());

        // 🛠️【修正】エンティティの定義（stock）に完全に合わせました
        product.setStock(form.getStock());

        // 3. 新規登録時は削除フラグを「0（未削除・有効）」に設定
        // 🛠️【修正】エンティティの定義（delete_flag）に完全に合わせ、閉じカッコのエラーを修正しました
        product.setDelete_flag(0);

        // 4. Repository（MyBatis）を呼び出してDBにインサートを実行
        productRegistrationRepository.insertProductRegistration(product);
    }
}