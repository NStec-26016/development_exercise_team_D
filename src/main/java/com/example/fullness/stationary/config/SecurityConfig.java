package com.example.fullness.stationary.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 全てのリクエスト（URL）に対して、ログインなしでのアクセスを無条件で許可する
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll())
                // CSRF対策やログイン画面への強制転送を一時的に無効化
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable());

        return http.build();
    }
}
