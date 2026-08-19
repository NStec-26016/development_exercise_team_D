package com.example.fullness.stationary.repository;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.fullness.stationary.entity.EmployeeAccount;

/**
 * 担当者アカウントに関するデータアクセスを管理するリポジトリインターフェース。
 * 社員アカウントテーブル（employee_account）、社員テーブル（employee）、
 * 部署テーブル（department）等の連携データを操作する。
 */
@Mapper
@Repository

public interface EmployeeAccountRepository {
    /**
     * 【UC17 担当者ログイン】
     * 入力されたアカウント名（ログイン名）をキーに、担当者のアカウント情報を1件取得する。
     * ログイン後のセッション情報として利用するため、紐づく「社員情報（名前・ふりがな）」
     * および「部署情報（部署名）」をテーブル結合（JOIN）した状態で取得する。
     * 
     * @param name ログイン画面から入力されたアカウント名（識別子）
     * @return 該当する {@link EmployeeAccount} オブジェクト。存在しない場合は {@code null}
     */
    EmployeeAccount findByName(@Param("name") String name);

    /**
     * ログイン失敗回数とロック日時を更新する。
     */
    void updateLockStatus(EmployeeAccount employeeAccount);

    // UC09 担当者アカウント登録
    int insertEmployeeAccount(EmployeeAccount employeeAccount);

    // ⬇️【あなたが新しく追記する1行】アカウント名の重複件数を数えるメソッド
    int countByName(@Param("name") String name);
}
