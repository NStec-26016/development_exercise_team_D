package com.example.fullness.stationary.form;

import jakarta.validation.constraints.PositiveOrZero;

import lombok.Data;

@Data
public class ProductRegistrationForm {

    @NotNull(message = "カテゴリを選択してください")
    private Integer productCategoryId;

    @NotBlank(message = "商品名を入力してください")
    private String name;

    @NotNull(message = "価格を入力してください")
    @PositiveOrZero(message = "価格は0以上の数値を入力してください")
    private Integer price;

    private String imageUrl;
}
