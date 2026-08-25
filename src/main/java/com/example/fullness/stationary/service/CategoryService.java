package com.example.fullness.stationary.service;

import com.example.fullness.stationary.repository.ProductCategoryRepository;
import com.example.fullness.stationary.entity.ProductCategory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    // 宣言部分
    private final ProductCategoryRepository productCategoryRepository;

    // コンストラクタ
    public CategoryService(ProductCategoryRepository productCategoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
    }

    /**
     * 商品カテゴリを新規登録するメソッド
     */
    @Transactional
    public void registerCategory(String name) {
        // 登録処理
        productCategoryRepository.insertCategory(name);
    }

    /**
     * カテゴリ名がすでに登録されているか検索・チェックするメソッド
     */
    public boolean isCategoryNameExists(String name) {
        // 重複チェック
        ProductCategory category = productCategoryRepository.findByName(name);
        return category != null;
    }
}
