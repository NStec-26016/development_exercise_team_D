package com.example.fullness.stationary.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.repository.ProductRepository;

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
}