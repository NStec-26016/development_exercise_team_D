package com.example.fullness.stationary.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryForm {

    // @NotBlank(message = "カテゴリ名を入力してください。")
    // @Size(max = 30, message = "カテゴリ名は1～30文字で入力してください")
    // private String name;
    @NotBlank(message = "カテゴリ名を入力してください。")
    @NotBlank(message = "カテゴリ名は1～30文字で入力してください")
    @Size(max = 30, message = "カテゴリ名は1～30文字で入力してください")
    private String name;
}
