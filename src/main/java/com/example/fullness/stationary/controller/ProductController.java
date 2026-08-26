package com.example.fullness.stationary.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.service.ProductService;

@Controller
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/admin/product")
    public String showProductList(
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @PageableDefault(size = 10, sort = "Id") Pageable pageable,
            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {
            model.addAttribute("loginEmployeeName", authentication.getName());
            model.addAttribute("loggedIn", true);
        } else {
            model.addAttribute("loggedIn", false);
        }

        try {
            model.addAttribute("categories", productService.findAllCategories());

            // HTML側（1始まり）とSpring（0始まり）のズレを調整する処理
            Pageable adjustedPageable = pageable;
            if (pageable.getPageNumber() > 0) {
                adjustedPageable = PageRequest.of(
                        pageable.getPageNumber() - 1,
                        pageable.getPageSize(),
                        pageable.getSort());
            }

            Page<Product> productPage = productService.findProductsByCategory(categoryId, adjustedPageable);

            model.addAttribute("products", productPage.getContent());
            model.addAttribute("currentPage", productPage.getNumber() + 1);
            model.addAttribute("totalPages", productPage.getTotalPages());
            model.addAttribute("selectedCategoryId", categoryId);

            if (productPage.getContent().isEmpty()) {
                model.addAttribute("message", "該当する商品情報がありません");
            }

        } catch (Exception e) {
            model.addAttribute("errorMessage", "商品情報の取得に失敗しました");
            System.err.println("[ERROR] 商品情報取得中にエラーが発生しました。詳細: " + e.getMessage());
        }

        return "admin/product/search";
    }

    // @GetMapping("/admin/product/add")
    // public String showAddForm(Model model) {
    // model.addAttribute("form", new
    // com.example.fullness.stationary.form.ProductRegistrationForm());
    // model.addAttribute("categories", productService.findAllCategories());
    // model.addAttribute("loggedIn", true);

    // return "admin/product/add_form";
    // }
}