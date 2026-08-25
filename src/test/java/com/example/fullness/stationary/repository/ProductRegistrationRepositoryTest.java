package com.example.fullness.stationary.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.fullness.stationary.entity.Product;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRegistrationRepositoryTest {

    @Autowired
    private ProductRegistrationRepository repository;

    @Test
    void ProductRegistrationRepositoryTest_OK() {
        // テストデータの準備
        Product product = new Product();
        product.setProductCategoryId(1);
        product.setName("えんぴつ");
        product.setPrice(70);
        product.setImageUrl("black_pen.jpg");

        // テストの実行
        int result = repository.insertProductRegistration(product);

        // 結果検証
        assertThat(result).isEqualTo(1);
    }
}
