package com.example.fullness.stationary.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * SpringSecurityフレームワークを用いたセキュリティの設定のクラスです。
 * 
 *
 * @author 長田
 * @version 1.0
 */

@EnableWebSecurity
@Configuration
public class SecurityConfig {
   /**
    * 【システムのセキュリティ制御およびアクセス権限の集中管理を実現。】
    * アプリケーション全体のHTTPリクエストに対する認証・認可ルール、ログイン・ログアウト挙動を一元的に定義します。
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
       * 担当者ログイン用URL（/admin/login）およびメニュー画面（/admin）、エラー画面（/error）は誰でもアクセスできるようにし、
       * それ以外のページはすべてログイン必須（認証が必要）に制限します。
       */
      http.csrf(csrf -> csrf.disable());
      http.authorizeHttpRequests(authz -> authz
            .requestMatchers("/error").permitAll()
            // CSSなどの静的リソースを許可する設定
            .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
            .requestMatchers("/admin", "/admin/login").permitAll()
            .anyRequest().authenticated())
            /*
             * 【ログインの実現】
             * 
             * 専用のログインフォームを通じてユーザーを識別し、認証成功時はメニュー画面へ遷移させます。
             * 認証に失敗した場合は、ログイン画面に留まらせ、不正な侵入を防止します。
             */
            .formLogin(login -> login
                  // ログイン認証画面を出力するURLパスを表します
                  .loginPage("/admin/login")
                  // 指定されたURLパスの場合ログイン認証することを表します
                  .loginProcessingUrl("/admin/login")
                  // ユーザ名とパスワードのリクエストパラメータ名を表します
                  .usernameParameter("accountName")
                  .passwordParameter("password")
                  // 認証が成功した場合にリダイレクトするURLパスを指定します。2番目の引数にはログインが成功したら必ず指定された
                  // パスに遷移させたい場合true を指定します
                  .defaultSuccessUrl("/admin", true)
                  // ログイン認証に失敗した場合に遷移するURLパスを表します
                  .failureUrl("/admin/login?error")
                  // ログイン認証の動作はいつでも許可することを表します
                  .permitAll())
            /*
             * 
             * 【ログアウトの実現】
             * 
             * ユーザーが利用を終了した際、サーバー側の通信状態（セッション）を即座に破棄し、ブラウザに残る識別情報（Cookie）や認証用キャッシュを完全に消去します。
             * 
             */
            .logout(logout -> logout
                  // 引数のURL パスの場合にログアウト処理することを表す
                  .logoutUrl("/logout")
                  // ログアウト処理が成功した場合の遷移先パスを表す
                  .logoutSuccessUrl("/admin")
                  // セッションを破棄することを表す
                  .invalidateHttpSession(true)
                  // 引数で指定された名称でCookie に保存されている値を破棄する
                  .deleteCookies("JSESSIONID")
                  // 認証情報をクリアすることを表す
                  .clearAuthentication(true)
                  // ログアウト処理は全員アクセス可能
                  .permitAll());

      return http.build();

   }

   /**
    * パスワード暗号化（ハッシュ化）の仕組みを定義する
    * ユーザーのパスワードを管理・照合する
    * 
    * @return システム全体で使用する PasswordEncoder（BCrypt形式）のオブジェクト
    */

   @Bean
   public PasswordEncoder passwordEncoder() {
      // TODO 後でハッシュ値を使うパターンに戻す
      // return new BCryptPasswordEncoder();

      return NoOpPasswordEncoder.getInstance();
   }

}
