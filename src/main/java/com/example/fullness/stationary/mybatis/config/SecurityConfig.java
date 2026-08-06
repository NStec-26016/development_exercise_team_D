package com.example.fullness.stationary.mybatis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 認証・認可セキュリティ設定クラス
 * 
 * @author 長田
 * @version 0.01
 */

@EnableWebSecurity
@Configuration
public class SecurityConfig {
   /**
    * セキュリティフィルターチェーンの設定
    * HTTPリクエストに対するセキュリティポリシーを定義する
    *
    * アクセス権限 (Authorize)
    * /admin/login : ログイン画面。全員アクセスを許可。
    * /admin : 管理者メニュー画面。ログイン前後どちらの状態でも全員アクセスを許可。
    * その他のURL : システム内のその他全画面（/menu等）。ログイン認証を必須とする。
    * 
    * ログイン設定 (FormLogin)
    * .loginPage("/admin/login") : カスタムログイン画面を表示するためのURL。
    * .loginProcessingUrl("/admin/login") : ログイン認証を処理する内部エンドポイント（POST）。
    * .usernameParameter("username") : フォームから送信されるユーザー名のname属性。
    * .passwordParameter("password") : フォームから送信されるパスワードのname属性。
    * .defaultSuccessUrl("/menu", true) : ログイン成功時のリダイレクト先（強制的に /menu へ遷移）。
    * .failureUrl("/admin/login?error") : ログイン失敗時のリダイレクト先。
    * 
    * ログアウト設定 (Logout)
    * .logoutUrl("/logout") : ログアウト処理を実行するための内部エンドポイント（POST）。
    * .logoutSuccessUrl("/admin") : ログアウト完了後のリダイレクト先（ログイン前のメニュー画面に戻す）。
    * .invalidateHttpSession(true) : サーバー側のHTTPセッションを完全に破棄。
    * .deleteCookies("JSESSIONID") : クライアント側のセッションクッキーを削除。
    * .clearAuthentication(true) : 認証情報（ユーザー情報）を完全にクリア。
    * 
    * @param http HTTPリクエストのセキュリティ構成を構築するオブジェクト
    * @return 構築された SecurityFilterChain オブジェクト
    * @throws Exception セキュリティ設定構築中にエラーが発生した場合
    */

   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      http.authorizeHttpRequests(authz -> authz
            .requestMatchers("/", "/admin", "/admin/login").permitAll()
            .anyRequest().authenticated())
            .formLogin(login -> login
                  // ログイン認証画面を出力するURLパスを表す
                  .loginPage("/admin/login")
                  // 指定されたURL パスの場合ログイン認証することを表す
                  .loginProcessingUrl("/admin/login")
                  // ユーザ名とパスワードのリクエストパラメータ名を表す
                  .usernameParameter("name")
                  .passwordParameter("password")
                  // 認証が成功した場合にリダイレクトするURLパスを指定する。2番目の引数にはログインが成功したら必ず指定された
                  // パスに遷移させたい場合true を指定する
                  .defaultSuccessUrl("/menu", true)
                  // ログイン認証に失敗した場合に遷移するURLパスを表す
                  .failureUrl("/admin/login?error")
                  // ログイン認証の動作はいつでも許可することを表す
                  .permitAll())
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
      return new BCryptPasswordEncoder();
   }

}
