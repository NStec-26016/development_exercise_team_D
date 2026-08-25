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

    @NotBlank(message = "商品名を入力してください")
    @Size(min = 2, max = 20, message = "商品名は2～20文字で入力してください")
    private String name;

    @NotNull(message = "価格を入力してください")
    @Min(value = 0, message = "価格は0以上の数値を入力してください")
    @Max(value = 1000000, message = "価格は100万円以下で入力してください")
    private Integer price;

    @NotNull(message = "在庫数を入力してください")
    @Min(value = 0, message = "在庫数は0以上の数値を入力してください")
    @Max(value = 1000, message = "在庫数は1000個以下で入力してください")
    private Integer stock;

    @NotNull(message = "カテゴリを選択してください")
    private Integer categoryId;

    private MultipartFile image;

    private String categoryName;
    private String imagePath;

    // :bulb:【最重要：DB保存用】MyBatisが #{imageUrl} を読み込みに来たとき、200文字以内の短いパスを自動で返却します
    public String getImageUrl() {
        return this.imagePath;
    }
}