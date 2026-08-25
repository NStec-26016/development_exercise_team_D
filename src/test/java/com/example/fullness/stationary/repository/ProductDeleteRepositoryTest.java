package com.example.fullness.stationary.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;

import com.example.fullness.stationary.dto.ProductDetailDto;
import com.example.fullness.stationary.entity.Product;

@MybatisTest

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductDeleteRepositoryTest {

    @Autowired
    private ProductDeleteRepository productDeleteRepository;

    @Test
    @Sql("/com/example/fullness/stationary/repository/ProductDeleteRepositoryTest.sql")
    @DisplayName("findById: 指定したIDの商品が1件取得できること")
    void testFindById_OK() {
        Product result = productDeleteRepository.findById(2);
        assertNotNull(result);
        assertEquals(2, result.getId());
        assertEquals("水性ボールペン(赤)", result.getName());
    }

    @Test
    @Sql("/com/example/fullness/stationary/repository/ProductDeleteRepositoryTest.sql")
    @DisplayName("findProductDetailById: 削除確認用の詳細情報(DTO)が正しく取得できること")
    void testFindProductDetailById_OK() {
        ProductDetailDto result = productDeleteRepository.findProductDetailById(2);
        assertNotNull(result);
        assertEquals(2, result.getId());
        assertEquals("水性ボールペン(赤)", result.getName());
        assertEquals(120, result.getPrice());
        assertEquals(10, result.getStock());
        assertEquals("文房具", result.getCategoryName());
        assertEquals("red.pen_w.jpg", result.getImagePath());
    }

    @Test
    @Sql("/com/example/fullness/stationary/repository/ProductDeleteRepositoryTest.sql")
    @DisplayName("deleteById: 指定したIDの商品のdelete_flagが1に更新されること")
    void testDeleteById_OK() {
        productDeleteRepository.deleteById(2);
        Product updatedProduct = productDeleteRepository.findById(2);
        assertNotNull(updatedProduct);
        assertEquals(1, updatedProduct.getDeleteFlag());
    }
}
