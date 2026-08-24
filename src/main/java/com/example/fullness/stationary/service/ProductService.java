package com.example.fullness.stationary.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fullness.stationary.dto.ProductDetailDto;
import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.repository.ProductCategoryRepository;
import com.example.fullness.stationary.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    // 商品をIDで1件検索する
    // public Product findById(Integer productId) {
    // return productRepository.findById(productId);
    // }

    // 商品をIDで1件削除（論理削除）する
    public void deleteById(Integer productId) {
        productRepository.deleteById(productId);
    }

    // カテゴリIDからデータベースの本物のカテゴリ名を取得する
    public String getCategoryNameById(Integer categoryId) {
        if (categoryId == null) {
            return "なし";
        }

        // カテゴリリポジトリを使って、IDからカテゴリの名前（文字列）を取得する
        String categoryName = productCategoryRepository.findNameByCategoryId(categoryId);

        // もしデータベースに名前が登録されていなかったら「不明」と返す
        return (categoryName != null) ? categoryName : "不明";
    }

    public ProductDetailDto getProductDetail(Integer productId) {
        // Repositoryを呼び出して、JOINされたデータをそのままControllerに流す
        return productRepository.findProductDetailById(productId);
    }

    public void deleteProduct(Integer productId) {
        // Repositoryのvoidメソッドを呼び出してフラグを1に更新する
        productRepository.deleteById(productId);
    }

}
