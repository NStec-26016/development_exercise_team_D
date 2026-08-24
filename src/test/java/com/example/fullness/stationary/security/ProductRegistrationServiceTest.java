package com.example.fullness.stationary.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    @DisplayName("商品登録が正常に成功すること")
    void registerProduct_Success() {
        // 1. テストデータの準備
        ProductRegistrationForm form = new ProductRegistrationForm();
        form.setCategoryId(1);
        form.setName("えんぴつ");
        form.setPrice(70);
        form.setImagePath("images/test.jpg");
        form.setStock(10);

        // 2. 引数をキャプチャする設定
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        // 3. テスト実行
        productRegistrationService.registerProduct(form);

        // 4. 検証
        verify(productRegistrationRepository, times(1)).insertProductRegistration(productCaptor.capture());

        Product capturedProduct = productCaptor.getValue();

        // 1行ずつシンプルに検証
        assertEquals(1, capturedProduct.getProduct_category_id());
        assertEquals("えんぴつ", capturedProduct.getName());
        assertEquals(70, capturedProduct.getPrice());
        assertEquals("images/test.jpg", capturedProduct.getImageUrl());
        assertEquals(10, capturedProduct.getStock());
        assertEquals(0, capturedProduct.getDelete_flag());
    }
}
