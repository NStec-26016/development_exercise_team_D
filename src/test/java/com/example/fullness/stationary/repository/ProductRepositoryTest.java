package com.example.fullness.stationary.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.fullness.stationary.entity.Product;

@SpringBootTest
@Transactional
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    // 1. countAll() に対応
    @Test
    void testCountAll_ReturnsTotalCount() {
        long count = productRepository.countAll();
        assertEquals(21L, count); // 21件の初期データとの一致検証
    }

    // 2. countByCategoryId() に対応
    @Test
    void testCountByCategoryId_WithValidCategoryId_ReturnsCategoryCount() {
        Integer categoryId = 1; // 文房具（14件）
        long count = productRepository.countByCategoryId(categoryId);
        assertEquals(14L, count);
    }

    // 3. findAllWithPaging() に対応（1ページ目）
    @Test
    void testFindAllWithPaging_FirstPage_ReturnsTenProducts() {
        List<Product> products = productRepository.findAllWithPaging(10, 0);
        assertNotNull(products);
        assertEquals(10, products.size());
    }

    // 4. findAllWithPaging() に対応（3ページ目・端数）
    @Test
    void testFindAllWithPaging_ThirdPage_ReturnsRemainingProduct() {
        List<Product> products = productRepository.findAllWithPaging(10, 20);
        assertNotNull(products);
        assertEquals(1, products.size());
    }

    // 5. findByCategoryIdWithPaging() に対応
    @Test
    void testFindByCategoryIdWithPaging_WithValidCategoryId_ReturnsFilteredProducts() {
        Integer categoryId = 2; // ガジェット（4件）
        List<Product> products = productRepository.findByCategoryIdWithPaging(categoryId, 10, 0);
        assertNotNull(products);
        assertEquals(4, products.size());
    }
}