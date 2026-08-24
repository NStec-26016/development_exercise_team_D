package com.example.fullness.stationary.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.entity.ProjectCategory;
import com.example.fullness.stationary.repository.ProductRepository;
import com.example.fullness.stationary.repository.ProjectCategoryRepository;

/**
 * 商品およびカテゴリに関するビジネスロジックを提供するサービスクラス。
 * 
 * @author Team_D
 * @version 1.0
 */
@Service
public class ProductService {

    // リポジトリのフィールド宣言
    private final ProjectCategoryRepository projectCategoryRepository;
    private final ProductRepository productRepository;

    /**
     * コンストラクタインジェクションにより、必要なリポジトリを注入します。
     */
    public ProductService(ProjectCategoryRepository projectCategoryRepository, ProductRepository productRepository) {
        this.projectCategoryRepository = projectCategoryRepository;
            
        this.productRepository = productRepository;
    }

    /**
     * カテゴリマスタから全カテゴリをカテゴリIDの昇順で取得します。
     */
    public List<ProjectCategory> findAllCategories() {
        return projectCategoryRepository.findAllByOrderByCategoryIdAsc();
    }

    /**
     * 指定されたカテゴリIDに基づいて商品をページング形式で取得します。
     */
    public Page<Product> findProductsByCategory(Integer categoryId, Pageable pageable) {
        int limit = pageable.getPageSize();
        long offset = pageable.getOffset();

        List<Product> content;
        long total;

        if (categoryId == null) {
            // 全件取得
            content = productRepository.findAllWithPaging(limit, offset);
            total = productRepository.countAll();
        } else {
            // カテゴリ絞り込み取得
            content = productRepository.findByCategoryIdWithPaging(categoryId, limit, offset);
            total = productRepository.countByCategoryId(categoryId);
        }

        return new PageImpl<>(content, pageable, total);
    }
}