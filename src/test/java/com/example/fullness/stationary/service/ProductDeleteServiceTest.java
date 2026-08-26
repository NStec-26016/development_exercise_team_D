package com.example.fullness.stationary.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.fullness.stationary.dto.ProductDetailDto;
import com.example.fullness.stationary.repository.ProductCategoryRepository;
import com.example.fullness.stationary.repository.ProductDeleteRepository;

@ExtendWith(MockitoExtension.class)
class ProductDeleteServiceTest {

    @InjectMocks
    private ProductDeleteService productDeleteService;

    @Mock
    private ProductDeleteRepository productDeleteRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Test
    @DisplayName("deleteById: 指定したIDの商品が論理削除されること")
    void testDeleteById_OK() {
        // Arrange
        Integer productId = 1;
        doNothing().when(productDeleteRepository).deleteById(productId);

        // Act
        productDeleteService.deleteById(productId);

        // Assert
        verify(productDeleteRepository, times(1)).deleteById(productId);
    }

    @Test
    @DisplayName("getCategoryNameById: カテゴリ名が正しく取得できること")
    void testGetCategoryNameById_OK() {
        // Arrange
        Integer categoryId = 10;
        String expectedName = "文房具";
        when(productCategoryRepository.findNameByCategoryId(categoryId)).thenReturn(expectedName);

        // Act
        String result = productDeleteService.getCategoryNameById(categoryId);

        // Assert
        assertEquals(expectedName, result);
        verify(productCategoryRepository, times(1)).findNameByCategoryId(categoryId);
    }

    @Test
    @DisplayName("getProductDetail: 商品詳細が正しく取得できること")
    void testGetProductDetail_OK() {
        // Arrange
        Integer productId = 1;

        ProductDetailDto mockDto = new ProductDetailDto();
        mockDto.setId(productId);
        mockDto.setName("水性ボールペン(赤)");
        mockDto.setPrice(120);
        mockDto.setStock(10);
        mockDto.setCategoryName("文房具");
        mockDto.setImageUrl("red.pen_w.jpg");

        when(productDeleteRepository.findProductDetailById(productId)).thenReturn(mockDto);

        // Act
        ProductDetailDto result = productDeleteService.getProductDetail(productId);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("水性ボールペン(赤)", result.getName());
        assertEquals(120, result.getPrice());
        assertEquals(10, result.getStock());
        assertEquals("文房具", result.getCategoryName());
        assertEquals("red.pen_w.jpg", result.getImageUrl());
        verify(productDeleteRepository, times(1)).findProductDetailById(productId);
    }

    @Test
    @DisplayName("deleteProduct: 指定したIDの商品が削除されること")
    void testDeleteProduct_OK() {
        // Arrange
        Integer productId = 1;
        doNothing().when(productDeleteRepository).deleteById(productId);

        // Act
        productDeleteService.deleteProduct(productId);

        // Assert
        verify(productDeleteRepository, times(1)).deleteById(productId);
    }
}
