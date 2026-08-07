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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view; // 💡 view().name() を使うためのインポート
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest // 💡 習った通りのアノテーション
public class LoginControllerTest {

    @Autowired
    LoginController loginController; // 💡 習った通りのインジェクション

    MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        // 💡 習ったサンプルと全く同じ、最もシンプルなスタンドアロン起動に戻します
        mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
    }

    /**
     * ケース1：ログイン画面の正常表示
     */
    @Test
    public void testShowLoginPage() throws Exception {
        // 【テスト内容】URL /admin/login にGETリクエストを送信する
        mockMvc.perform(get("/admin/login"))
                .andExpect(status().isOk())
                // 【期待結果】return "login" (無限ループを起こさない安全な検証方法)
                .andExpect(view().name("admin/login"));
    }

    /**
     * ケース2：ログイン状態でのメニュー表示
     */
    @Test
    public void testShowMenuPageLoggedIn() throws Exception {
        // SecurityContextHolderに「testUser」がログインした状態を強制セットする調整
        org.springframework.security.core.Authentication auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                "testUser", "password");
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);

        try {
            // 【テスト内容】URL /admin にGETリクエストを送信する
            MvcResult mvcResult = mockMvc.perform(get("/admin"))
                    .andExpect(status().isOk())
                    // 【期待結果】return "menu"
                    .andExpect(view().name("menu"))
                    .andReturn(); // 習った通り andReturn() で結果を回収

            // 習った通りの手順で ModelMap を取得してアサーション
            ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
            String name = (String) modelMap.get("name"); // Entity仕様のキー名「"name"」

            // 【期待結果】Modelにログインしたユーザー名「testUser」が正しく設定されていることを確認
            Assertions.assertEquals("testUser", name);

        } finally {
            // テスト終了後にログイン状態をクリア
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
                .andExpect(view().name("menu"))
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
                .andExpect(view().name("admin/login"));
    }
}
