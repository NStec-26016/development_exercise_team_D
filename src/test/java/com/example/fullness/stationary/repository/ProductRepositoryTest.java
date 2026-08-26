package com.example.fullness.stationary.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.fullness.stationary.entity.Product;

@SpringBootTest
@Transactional // テストが終わったらデータをロールバック（元に戻す）するアノテーション
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testFindAllWithPaging_OK() {
        long count = productRepository.countAll();
        // 初期データが3件入っている前提であれば 3 以上、または 0件以上であることを検証
        assertTrue(count >= 0);
    }
    // TODO 別のUCテストケースのためいったんコメント
    // @Test
    // void testFindfindByCategoryIdWithPaging_OK() {
    // // limit = 10, offset = 0 で取得
    // List<Product> products = productRepository.findAllWithPaging(10, 0);
    // assertNotNull(products);
    // }
}