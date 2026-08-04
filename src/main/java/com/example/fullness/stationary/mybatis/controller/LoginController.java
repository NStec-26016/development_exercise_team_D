package com.example.fullness.stationary.mybatis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class LoginController {
    
    @Autowired
    // private LoginForm loginForm;
    
    /**ログイン画面を表示*/
    @GetMapping("/login")
    public String showLogin(Model model){
        return null;
    }
    
    
    // (@ModelAttribute LoginForm loginform,
    // model,model)


    /**ログインボタンを押下 */

}
