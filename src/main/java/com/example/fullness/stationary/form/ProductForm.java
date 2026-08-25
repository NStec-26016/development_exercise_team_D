package com.example.fullness.stationary.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data; // 💡Lombokのインポートを追加

/**
 * 商品修正画面の入力値を保持し、バリデーションを行うフォームクラス。
 */
@Data // 💡これだけで、すべてのフィールドの Getter / Setter が自動生成されます！
public class ProductForm {

    /** 商品ID（修正対象を特定するために必須） */
    private Integer id;

    /** 商品名 */
    @NotBlank(message = "商品名を入力してください。")
    private String name;

    /** 価格 */
    @NotNull(message = "価格を入力してください。")
    private Integer price;

    /** 備考（設計書の例外シナリオ：制約違反チェック用） */
    private String remarks;
    /** 在庫数（追加） */
    private Integer stock;

}
