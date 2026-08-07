package com.example.fullness.stationary.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.example.fullness.stationary.mybatis.config.SecurityConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LoginController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration.class
})
@Import(SecurityConfig.class)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc; // 擬似的にHTTPリクエストを送信するオブジェクト

    /**
     * Thymeleafの読込エラーと無限ループ（循環参照エラー）を同時に防ぐためのテスト用設定
     */
    @TestConfiguration
    static class TestConfig {
        @Bean
        public InternalResourceViewResolver defaultViewResolver() {
            InternalResourceViewResolver resolver = new InternalResourceViewResolver();
            resolver.setPrefix("");
            resolver.setSuffix("");
            return resolver;
        }
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "testUser") // 💡
                                                                                           // ログイン状態（ユーザー名：testUser）を擬似的に作り出す
    public void testShowMenuPageLoggedIn() throws Exception {
        // 【テスト内容】URL /admin にGETリクエストを送信する
        org.springframework.test.web.servlet.MvcResult mvcResult = mockMvc.perform(get("/admin"))
                // ステータスコードが 200 OK であることを確認
                .andExpect(status().isOk())
                // 【期待結果】戻り値のビュー名が "menu" であることを確認
                .andExpect(forwardedUrl("menu"))
                .andReturn();

        org.springframework.ui.ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String name = (String) modelMap.get("name");

        // Modelにログインしたユーザー名が正しく設定されているか確認
        org.junit.jupiter.api.Assertions.assertEquals("testUser", name);
    }

    @Test
    // @WithMockUser をあえて付けないことで、未ログイン状態（ゲスト）を再現
    public void testShowMenuPageNotLoggedIn() throws Exception {
        // 【テスト内容】URL /admin にGETリクエストを送信する
        org.springframework.test.web.servlet.MvcResult mvcResult = mockMvc.perform(get("/admin"))
                // ステータスコードが 200 OK であることを確認
                .andExpect(status().isOk())
                // 【期待結果】戻り値のビュー名が "menu" であることを確認
                .andExpect(forwardedUrl("menu"))
                .andReturn();

        org.springframework.ui.ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String name = (String) modelMap.get("name"); // 💡 Entity仕様に合わせたキー名「name」

        org.junit.jupiter.api.Assertions.assertNull(name);
    }

    @Test
    public void testShowLoginPageWithError() throws Exception {
        // 【テスト内容】URL /admin/login?error にGETリクエストを送信する
        // .param("error", "") を使うことで、URLの末尾に「?error」を付与した状態を再現します
        mockMvc.perform(get("/admin/login").param("error", ""))
                // ステータスコードが 200 OK であることを確認
                .andExpect(status().isOk())
                // 【期待結果】戻り値のビュー名が "login" であることを確認
                .andExpect(forwardedUrl("login"));
    }

}
