package com.example.fullness.stationary.mybatis.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

    /**
    * Javadoc用コメントのテストプログラム
    * @author　紺谷、長田
    */

    /**
     * @Controller コントローラークラス
     *@RequestMapping  このコントローラーのベースとなるリクエストURLをマッピングする。
     *@SessionAttributes 複数のリクエスト間で維持するモデル属性（セッションスコープのデータ）を指定する。
     */
    
    public class LogoutController {

    /**
    *ログアウトボタンが押下されたときに呼び出され、
    *ログアウト処理を実行した後にメニュー画面へ遷移する。
    *@param request HTTPリクエスト情報
    *@return 遷移先画面のパス( /admin)
     */


    //  @GetMapping()
    // //  セッション削除
    //  public String logout(HttpSession session){

    //     session.invalidate();
    //  }

    public String logout(HttpServletRequest request){
        //処理を実装
        return "redirect:/admin";
    }
}


