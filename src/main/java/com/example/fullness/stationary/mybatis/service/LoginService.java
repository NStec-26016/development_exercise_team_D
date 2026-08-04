package com.example.fullness.stationary.mybatis.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
    /**照合する */
@Service
public class LoginService implements Serializable {
 // コンストラクタ注入
    @Autowired
 private EmployeeMapper EmployeeMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. MyBatis経由でDBからユーザーを取得
        User user = userMapper.findByUsername(username);
        
        if (user == null) {
            throw new UsernameNotFoundException("ユーザーが見つかりません: " + username);
        }

        // 2. Spring Securityが認識できるUserDetailsオブジェクトに変換して返す
        UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(username);
        builder.password(user.getPassword());
        builder.roles("USER"); // 簡易的にロールを固定付与
        
        return builder.build();
    }
}
