package com.example.fullness.stationary.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.form.ProductRegistrationForm;
import com.example.fullness.stationary.repository.ProductRegistrationRepository;

@ExtendWith(MockitoExtension.class)
class ProductRegistrationServiceTest {

    @Mock
    private ProductRegistrationRepository productRegistrationRepository;

    @InjectMocks
    private ProductRegistrationService productRegistrationService;

    // 1️⃣ ■ パターン1：【正常系】画像が正しく選択されて登録されるテスト

    @Test
    @DisplayName("正常系（パターン1）：画像が選択されている場合、Entityへ正しく値が詰め替えられ、200文字以内のパスでリポジトリが一度のみ呼び出されること")
    void testRegisterProduct_WithImage_OK() {
        ProductRegistrationForm testForm = new ProductRegistrationForm();
        testForm.setCategoryId(1);
        testForm.setName("えんぴつ");
        testForm.setPrice(70);
        testForm.setStock(10);
        testForm.setImagePath("black_pen_o.jpg");

        productRegistrationService.registerProduct(testForm);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRegistrationRepository, times(1)).insertProductRegistration(productCaptor.capture());

        Product actualProduct = productCaptor.getValue();
        assertEquals(1, actualProduct.getProductCategoryId());
        assertEquals("えんぴつ", actualProduct.getName());
        assertEquals(70, actualProduct.getPrice());
        assertEquals("black_pen_o.jpg", actualProduct.getImageUrl());
        assertEquals(10, actualProduct.getStock());
        assertEquals(0, actualProduct.getDeleteFlag());
    }

}
