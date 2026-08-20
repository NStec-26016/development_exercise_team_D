package com.example.fullness.stationary.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.fullness.stationary.entity.Category;
import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.repository.CategoryRepository;
import com.example.fullness.stationary.repository.ProductRepository;

/**
 * 商品およびカテゴリに関するビジネスロジックを提供するサービスクラス。
 * 
 * @author Team_D
 * @version 1.0
 */
@Service
public class ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    /**
     * コンストラクタインジェクションによりリポジトリを注入します。
     */
    public ProductService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    /**
     * カテゴリマスタから全カテゴリをカテゴリIDの昇順で取得します。
     * 
     * @return カテゴリのリスト
     */
    public List<Category> findAllCategories() {
        return categoryRepository.findAllByOrderByCategoryIdAsc();
    }

    /**
     * 指定されたカテゴリIDに基づいて商品をページング形式で取得します。
     * カテゴリIDがnullの場合は全商品を取得します。
     * 
     * @param categoryId 検索対象のカテゴリID（nullの場合は全件）
     * @param pageable   ページネーション情報
     * @return 商品のページオブジェクト
     */
    public Page<Product> findProductsByCategory(Integer categoryId, Pageable pageable) {
        int limit = pageable.getPageSize();
        long offset = pageable.getOffset();

        List<Product> content;
        long total;

        if (categoryId == null) {
            // 初期表示時は全商品をページング取得
            content = productRepository.findAllWithPaging(limit, offset);
            total = productRepository.countAll();
        } else {
            // カテゴリで絞り込んでページング取得
            content = productRepository.findByCategoryIdWithPaging(categoryId, limit, offset);
            total = productRepository.countByCategoryId(categoryId);
        }

        // MyBatisのListと総件数から、SpringのPageオブジェクトを生成して返却
        return new PageImpl<>(content, pageable, total);
    }
}