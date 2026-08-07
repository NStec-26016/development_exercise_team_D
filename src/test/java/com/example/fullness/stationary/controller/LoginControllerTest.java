package com.example.fullness.stationary.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class LoginControllerTest {

    @Autowired
    LoginController loginController;

    MockMvc mockMvc;

    @BeforeEach
    public void setUp() {

        org.springframework.web.servlet.view.InternalResourceViewResolver viewResolver = new org.springframework.web.servlet.view.InternalResourceViewResolver();

        // 1. 画面の場所をダミーの文字列で指定
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".html");

        viewResolver.setAlwaysInclude(true);

        mockMvc = org.springframework.test.web.servlet.setup.MockMvcBuilders
                .standaloneSetup(loginController)
                .setViewResolvers(viewResolver)
                .build();
    }

    /**
     * ケース1：ログイン画面の正常表示
     */
    @Test
    public void testShowLoginPage() throws Exception {
        // 【テスト内容】URL /admin/login にGETリクエストを送信する
        mockMvc.perform(get("/admin/login"))
                .andExpect(status().isOk())
                // 【期待結果】return "login"
                .andExpect(forwardedUrl("login"));
    }

    /**
     * ケース2：ログイン状態でのメニュー表示
     */
    @Test
    public void testShowMenuPageLoggedIn() throws Exception {

        org.springframework.security.core.Authentication auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                "testUser", "password");
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);

        try {
            // 【テスト内容】URL /admin にGETリクエストを送信する
            MvcResult mvcResult = mockMvc.perform(get("/admin"))
                    .andExpect(status().isOk())
                    // 【期待結果】return "menu"
                    .andExpect(forwardedUrl("menu"))
                    .andReturn();

            ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
            String name = (String) modelMap.get("name"); // コントローラーの実装通り、キー名は「"name"」

            // 【期待結果】Modelにログインしたユーザー名「testUser」が正しく設定されていることを確認
            Assertions.assertEquals("testUser", name);

        } finally {
            // テストが終わったら、他のテストに影響が出ないようにログイン状態をクリア
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
        }
    }

    /**
     * ケース3：未ログイン状態でのメニュー表示
     */
    @Test
    public void testShowMenuPageNotLoggedIn() throws Exception {
        // 【テスト内容】URL /admin にGETリクエストを送信する
        MvcResult mvcResult = mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                // 【期待結果】return "menu"
                .andExpect(forwardedUrl("menu"))
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String name = (String) modelMap.get("name");

        // 未ログイン時はModelに名前が設定されない（nullである）ことを検証
        Assertions.assertNull(name);
    }

    /**
     * ケース4：ログイン失敗時の画面表示（エラー系）
     */
    @Test
    public void testShowLoginPageWithError() throws Exception {
        // 【テスト内容】URL /admin/login?error にGETリクエストを送信する

        mockMvc.perform(get("/admin/login").param("error", ""))
                .andExpect(status().isOk())
                // 【期待結果】return "login"
                .andExpect(forwardedUrl("login"));
    }
}
