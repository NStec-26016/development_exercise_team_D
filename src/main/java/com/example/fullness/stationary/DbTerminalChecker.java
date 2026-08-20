package com.example.fullness.stationary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
public class DbTerminalChecker implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("==========================================");
        System.out.println("★ DB接続テストを開始します ★");
        System.out.println("==========================================");

        try {
            // 1. 接続できていると思われるSQL文（テーブル名・カラム名は適宜変更してください）
            String sql = "select * from employee_account";

            // 2. クエリを実行して、汎用的なMapのリストとして取得（型定義用のクラスすら不要です）
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

            System.out.println("【成功】DB接続に成功しました！データを表示します：");

            // 3. ループ処理でターミナルに1行ずつ出力
            for (Map<String, Object> row : rows) {
                System.out.printf("ID: %s | 名前: %s%n", row.get("id"), row.get("name"));
            }

        } catch (Exception e) {
            System.out.println("【失敗】DBへの接続、またはSQLの実行に失敗しました。");
            System.out.println("エラー原因: " + e.getMessage());
        }

        System.out.println("==========================================");
    }
}
