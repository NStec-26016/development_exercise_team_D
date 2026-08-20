package com.example.fullness.stationary.form;

import java.io.Serializable;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ProductRegistrationForm implements Serializable {

    // 1. 商品名（必須入力、2～20文字）
    @NotBlank(message = "商品名を入力してください")
    @Size(min = 2, max = 20, message = "商品名は2～20文字で入力してください")
    private String name;

    // 2. 単価（必須入力、数値形式、100万円以下）
    @NotNull(message = "価格を入力してください")
    @Min(value = 0, message = "価格は0以上の数値を入力してください")
    @Max(value = 1000000, message = "価格は100万円以下で入力してください")
    private Integer price;

    // 3. 在庫数（必須入力、数値形式、1000個以下）
    @NotNull(message = "在庫数を入力してください")
    @Min(value = 0, message = "在庫数は0以上の数値を入力してください")
    @Max(value = 1000, message = "在庫数は1000個以下で入力してください")
    private Integer stock;

    // 4. カテゴリID（必須選択）
    @NotNull(message = "カテゴリを選択してください")
    private Integer categoryId;

    // 5. 画像（必須アップロード）
    private MultipartFile image;

    // 6. 確認画面のHTML（${form.categoryName} や ${form.imagePath}）の表示用変数
    private String categoryName;
    private String imagePath;
}
