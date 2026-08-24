package com.example.fullness.stationary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.fullness.stationary.dto.ProductDetailDto;
import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.service.ProductService;

@Controller
@RequestMapping("/admin/product")
public class ProductDeleteController {

    @Autowired
    private ProductService productService;

    @GetMapping("/delete/{productId}")
    public String showProductDeleteConfirmPage(@PathVariable("productId") Integer productId, Model model) {
        // DTOをServiceから取得
        ProductDetailDto productDetail = productService.getProductDetail(productId);

        // HTML側が「product.imagePath」や「product.categoryName」を探せるように、DTOを丸ごと渡す
        model.addAttribute("product", productDetail);

        return "admin/product/delete_confirm";
    }

    @PostMapping("/delete/{productId}")
    public String executeDelete(@PathVariable("productId") Integer productId,
            Model model) {

        // Product product = productService.findById(productId);

        // 完了画面のHTML（${productName}）に商品名を渡すためにModelに登録する
        // model.addAttribute("productName", product.getName());
        ProductDetailDto productDetail = productService.getProductDetail(productId);
        String productName = (productDetail != null) ? productDetail.getName() : "不明な商品";

        // データベースから該当の商品を実際に削除する
        productService.deleteProduct(productId);

        // 完了画面のHTML（${productName}）に商品名を届ける
        model.addAttribute("productName", productName);

        // 「商品削除（完了）」のHTMLファイルを呼び出す
        return "admin/product/delete_complete";

    }

}
