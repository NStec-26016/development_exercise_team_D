package com.example.fullness.stationary.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.form.ProductForm;
import com.example.fullness.stationary.repository.ProductRepository;
import com.example.fullness.stationary.repository.ProductStockRepository;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * カテゴリ一覧を全件取得します。
     */
    public List<ProductCategory> findAllCategories() {
        return productRepository.findAllCategories();
    }

    /**
     * カテゴリIDとページング条件に応じて商品を検索します。
     */
    public Page<Product> findProductsByCategory(Integer categoryId, Pageable pageable) {
        int limit = pageable.getPageSize();
        long offset = pageable.getOffset();

        List<Product> content;
        long total;

        if (categoryId == null) {
            content = productRepository.findAllWithPaging(limit, offset);
            total = productRepository.countAll();
        } else {
            content = productRepository.findByCategoryIdWithPaging(categoryId, limit, offset);
            total = productRepository.countByCategoryId(categoryId);
        }

        return new PageImpl<>(content, pageable, total);
    }

    @Autowired
    private ProductStockRepository productStockRepository; // :bulb:追加：在庫用リポジトリを注入

    /**
     * 商品名がすでに登録されているかチェックする（重複チェック用）
     */
    public boolean isProductNameExists(String name) {
        Product product = productRepository.findByName(name);
        return product != null;
    }

    /**
     * 商品情報を更新（修正）する
     * :bulb:変更：商品の基本情報と在庫情報をまとめて1つのトランザクションで更新します。
     */
    public void updateProduct(ProductForm form) {
        // 1. 商品基本情報（名前・価格）の更新
        Product product = new Product();
        product.setId(form.getId());
        product.setName(form.getName());
        product.setPrice(form.getPrice());
        product.setProductCategoryId(form.getCategoryId());

        // :bulb:【追加】入力画面から届いた画像URL（imagePath）をエンティティの imageUrl へ確実に引き渡します！
        // product.setImageUrl(form.getImagePath());

        productRepository.updateProduct(product);

        // 2. 在庫情報の更新（formから取得した在庫数を反映）
        // :bulb:重要：前述の通り、ProductFormクラスに「private Integer stock;」を追加しておいてください。
        // コントローラー内の拡張クラス（ThymeleafExtendedForm）から値が引き継がれます。
        if (form.getId() != null && form.getStock() != null) {
            productStockRepository.updateStock(form.getId(), form.getStock());
        }
    }
}