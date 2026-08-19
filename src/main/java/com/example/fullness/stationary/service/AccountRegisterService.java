// package com.example.fullness.stationary.service;

// import com.example.fullness.stationary.entity.Employee;
// import com.example.fullness.stationary.form.AccountRegisterForm;
// import org.springframework.stereotype.Service;
// import java.util.ArrayList;
// import java.util.List;

// @Service
// public class AccountRegisterService {

// /**
// * アカウントが未登録の社員リストをシミュレート
// * 【結合時の修正ポイント】DBと繋ぐ際は、employeeRepository.findUnregistered() 等を呼び出すように書き換えます。
// */
// public List<Employee> getUnregisteredEmployees() {
// List<Employee> list = new ArrayList<>();

// // 動作テスト用のダミー社員を3名作成（画面のセレクトボックスに並びます）
// list.add(new Employee("EMP001", "佐藤 勝利"));
// list.add(new Employee("EMP002", "鈴木 一郎"));
// list.add(new Employee("EMP003", "高橋 誠"));

// // ★もし「未登録社員なし」の例外ケースをテストしたい場合は、
// // 上のlist.addをすべてコメントアウトして空のリスト（return list;）にしてください。

// return list;
// }

// /**
// * 選択されたIDから社員名を取得
// */
// public String getEmployeeNameById(String id) {
// for (Employee emp : getUnregisteredEmployees()) {
// if (emp.getId().equals(id)) {
// return emp.getName();
// }
// }
// return "未選択";
// }

// /**
// * アカウント名の重複チェックシミュレーター
// * ★テスト仕様: 画面で「admin」または「user」と入力すると、わざと重複エラー（既に使用されています）を起こせます。
// * 【結合時の修正ポイント】DB結合時は、employeeAccountRepository.countByAccountName()
// * 等で実際のレコード数を調べるように書き換えます。
// */
// public boolean isAccountNameDuplicate(String accountName) {
// if ("admin".equalsIgnoreCase(accountName) ||
// "user".equalsIgnoreCase(accountName)) {
// return true; // 重複エラーを発生させる
// }
// return false; // 重複なし
// }

// /**
// * アカウント登録処理（擬似トランザクション）
// * 【結合時の修正ポイント】
// * 1. ログインシステム側の「PasswordEncoder」を@Autowiredして、パスワードをハッシュ化（encode）します。
// * 2. ハッシュ化した値をEmployeeAccountエンティティに詰め、employeeAccountRepository.insert()
// * でDB保存します。
// */
// public void register(AccountRegisterForm form) {
// // コンソールに処理結果をログ出力
// System.out.println("--- 【模擬登録を実行しました】 ---");
// System.out.println("社員ID: " + form.getEmployeeId());
// System.out.println("社員名: " + form.getEmployeeName());
// System.out.println("アカウント名: " + form.getAccountName());
// System.out.println("パスワード（生）: " + form.getPassword());
// System.out.println("※結合時はここでSpring Securityを用いてハッシュ化を行い、MyBatisでDBへ保存します。");
// System.out.println("---------------------------------");
// }
// }