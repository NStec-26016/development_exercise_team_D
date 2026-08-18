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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.web.context.WebApplicationContext; // ★追加

@SpringBootTest
public class LoginControllerTest {

    // 1. loginControllerの代わりにWebApplicationContextをインジェクトし、
    // Spring Security（ログイン機能）」をテスト環境でも本物と同じように動かす
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    /**
     * ケース1：ログイン画面の正常表示
     */
    @Test
    public void testShowLoginPage() throws Exception {
        mockMvc.perform(get("/admin/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"));
    }

    /**
     * ケース2：ログイン状態でのメニュー表示
     */
    @Test
    public void testShowMenuPageLoggedIn() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/admin").with(user("testUser")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/menu"))
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String name = (String) modelMap.get("name");

        Assertions.assertEquals("testUser", name);
    }

    /**
     * ケース3：未ログイン状態でのメニュー表示
     */
    @Test
    public void testShowMenuPageNotLoggedIn() throws Exception {
        // 【テスト内容】URL /admin に未ログイン状態（.with(user(...))なし）でGETリクエストを送信する
        MvcResult mvcResult = mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                // 【期待結果】画面名は "admin/menu"
                .andExpect(view().name("admin/menu"))
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String name = (String) modelMap.get("name");
        Boolean loggedIn = (Boolean) modelMap.get("loggedIn");

        // 【期待結果】未ログイン時はModelに名前が設定されない（nullである）ことを検証
        Assertions.assertNull(name);

        // 【期待結果】loggedInフラグが false であることを検証
        Assertions.assertEquals(false, loggedIn);
    }

    /**
     * ケース4：ログイン失敗時の画面表示（エラー系）
     */
    @Test
    public void testShowLoginPageWithError() throws Exception {
        mockMvc.perform(get("/admin/login").param("error", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"));
    }
}
