package com.example.fullness.stationary.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.repository.ProductCategoryRepository;
import com.example.fullness.stationary.repository.ProductRepository;

class ProductServiceTest {

    @Mock
    private ProductCategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindfindAllCategories_OK() {
        // 準備
        List<ProductCategory> mockCategories = Arrays.asList(new ProductCategory(), new ProductCategory());
        when(categoryRepository.findAllByOrderByCategoryIdAsc()).thenReturn(mockCategories);

        // 実行
        List<ProductCategory> result = productService.findAllCategories();

        // 検証
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(categoryRepository, times(1)).findAllByOrderByCategoryIdAsc();
    }

    @Test
    void testFindfindProductsByCategory_OK() {
        // 準備
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> mockProducts = Arrays.asList(new Product(), new Product());

        when(productRepository.findAllWithPaging(anyInt(), anyLong())).thenReturn(mockProducts);
        when(productRepository.countAll()).thenReturn(2L);

        // 実行
        Page<Product> result = productService.findProductsByCategory(null, pageable);

        // 検証
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(productRepository, times(1)).findAllWithPaging(anyInt(), anyLong());
        verify(productRepository, times(1)).countAll();
    }

    @Test
    void testFindfindProductsByCategory_OK2() {
        // 準備
        Integer categoryId = 1;
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> mockProducts = Arrays.asList(new Product());

        when(productRepository.findByCategoryIdWithPaging(eq(categoryId), anyInt(), anyLong()))
                .thenReturn(mockProducts);
        when(productRepository.countByCategoryId(categoryId)).thenReturn(1L);

        // 実行
        Page<Product> result = productService.findProductsByCategory(categoryId, pageable);

        // 検証
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productRepository, times(1)).findByCategoryIdWithPaging(eq(categoryId), anyInt(), anyLong());
        verify(productRepository, times(1)).countByCategoryId(categoryId);
    }
}