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

import com.example.fullness.stationary.entity.Category;
import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.repository.CategoryRepository;
import com.example.fullness.stationary.repository.ProductRepository;

class ProductServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllCategories_OK() {
        // テストデータ
        // カテゴリマスタデータ（全4種類のカテゴリリスト）
        List<Category> mockCategories = Arrays.asList(new Category(), new Category(), new Category(), new Category());
        when(categoryRepository.findAllByOrderByCategoryIdAsc()).thenReturn(mockCategories);

        // テスト内容
        // すべての商品カテゴリマスタを取得する
        List<Category> result = productService.findAllCategories();

        // 結果
        // 返却されたリストがnullではなく、期待するサイズ4と実際のリストのサイズが一致すること
        assertNotNull(result);
        assertEquals(4, result.size());
        verify(categoryRepository, times(1)).findAllByOrderByCategoryIdAsc();
    }

    @Test
    void findProducts_NullCategory_OK() {
        // テストデータ
        // カテゴリID未指定（categoryId = null）、ページング情報（size=10, page=0）
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> mockProducts = Arrays.asList(new Product(), new Product()); // 1ページ分のモック商品データ

        when(productRepository.findAllWithPaging(anyInt(), anyLong())).thenReturn(mockProducts);
        when(productRepository.countAll()).thenReturn(21L); // 総件数21件の前提

        // テスト内容
        // カテゴリを指定せずに商品一覧のページング処理を実行する
        Page<Product> result = productService.findProductsByCategory(null, pageable);

        // 結果
        // 返却されたPageオブジェクトがnullではなく、総件数の期待値21Lと実際の総要素数が一致すること
        assertNotNull(result);
        assertEquals(21L, result.getTotalElements());
        assertEquals(10, result.getSize());
        verify(productRepository, times(1)).findAllWithPaging(10, 0L);
        verify(productRepository, times(1)).countAll();
    }

    @Test
    void findProducts_WithCategory_OK() {
        // テストデータ
        // カテゴリID指定（categoryId = 1：文房具）、ページング情報（size=10, page=0）
        Integer categoryId = 1;
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> mockProducts = Arrays.asList(new Product()); // 該当カテゴリのモック商品データ

        when(productRepository.findByCategoryIdWithPaging(eq(categoryId), anyInt(), anyLong()))
                .thenReturn(mockProducts);
        when(productRepository.countByCategoryId(categoryId)).thenReturn(14L); // 文房具の件数14件の前提

        // テスト内容
        // カテゴリ（文房具）を指定して商品一覧のページング処理を実行する
        Page<Product> result = productService.findProductsByCategory(categoryId, pageable);

        // 結果
        // 返却されたPageオブジェクトがnullではなく、総件数の期待値14Lと実際の総要素数が一致すること
        assertNotNull(result);
        assertEquals(14L, result.getTotalElements());
        verify(productRepository, times(1)).findByCategoryIdWithPaging(eq(categoryId), anyInt(), anyLong());
        verify(productRepository, times(1)).countByCategoryId(categoryId);
    }
}