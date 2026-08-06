package com.example.fullness.stationary.mybatis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * SpringSecurityフレームワークを用いたセキュリティの設定のクラス
 * 
 * 
 * 
 *
 * @author 長田
 * @version 0.01
 */

@EnableWebSecurity
@Configuration
public class SecurityConfig {
   /**
    * 【システムのセキュリティ制御およびアクセス権限の集中管理を実現。】
    * アプリケーション全体のHTTPリクエストに対する認証・認可ルール、ログイン・ログアウト挙動を一元的に定義する。
    *
    * @param http セキュリティポリシーを定義・構築するための構成オブジェクト。nullは指定できません。
    * @return 認可・ログイン・ログアウトの連動機能が統合された、システム全体の防御フィルター。
    * @throws Exception 定義したセキュリティ要件（ログイン制限やパスの設定など）に矛盾や不正な不整合が検出された場合。
    */
   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

      /*
       * 【未ログイン状態でもアクセスできる画面設定の実現】
       * 
       * 担当者ログイン用URL（/admin/login）およびメニュー画面（/admin）は誰でもアクセスできるようにし、
       * それ以外のページはすべてログイン必須（認証が必要）に制限します
       */
      http.authorizeHttpRequests(authz -> authz
            .requestMatchers("/admin/login").permitAll()
            .requestMatchers("/admin").permitAll()
            .anyRequest().authenticated())
            /*
             * 【ログインの実現】
             * 
             * 専用のログインフォームを通じてユーザーを識別し、認証成功時はメニュー画面へ遷移させます。
             * 認証に失敗した場合は、ログイン画面に留まらせ、不正な侵入を防止します。
             */

            .formLogin(login -> login
                  .loginPage("/admin/login")
                  .loginProcessingUrl("/admin/login")
                  .usernameParameter("name")
                  .passwordParameter("password")
                  .defaultSuccessUrl("/menu", true)
                  .failureUrl("/admin/login?error")
                  .permitAll())

            /*
             * 【ログアウトの実現】
             * 
             * ユーザーが利用を終了した際、サーバー側の通信状態（セッション）を即座に破棄し、ブラウザに残る識別情報（Cookie）や認証用キャッシュを完全に消去します。
             * 
             */
            .logout(logout -> logout
                  .logoutUrl("/logout")
                  .logoutSuccessUrl("/admin")
                  .invalidateHttpSession(true)
                  .deleteCookies("JSESSIONID")
                  .clearAuthentication(true)
                  .permitAll());

      return http.build();

   }

   /**
    * 【ユーザーの生パスワードが解読防止を実現。】
    * ハッシュ化技術をシステムに導入する。
    * これにより、開発者やシステム管理者であってもユーザーの生パスワードを閲覧することが不可能になる。
    * 結果として、悪意ある第三者による攻撃からユーザーのアカウント情報を保護する。
    * 
    * @return 安全にパスワードを保管・照合するための暗号化エンジン。システム内での登録・認証処理で共通して利用されます。
    */
   @Bean
   public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
   }

}
