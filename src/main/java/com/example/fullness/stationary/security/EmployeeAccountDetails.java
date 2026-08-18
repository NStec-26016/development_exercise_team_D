package com.example.fullness.stationary.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.fullness.stationary.entity.EmployeeAccount;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Spring Security用のユーザー情報を管理するクラスです。
 * 
 * データベースから取得した従業員アカウントの情報（EmployeeAccount）を、
 * Spring Securityがログイン処理で使えるようにするために作成しました。
 * 一度セットしたデータが変わらないように final をつけています。
 * 
 * @author 丸本
 * @version 1.0
 */
public class EmployeeAccountDetails implements UserDetails {

    /** データベースから持ってきた従業員アカウントの情報です */
    private final EmployeeAccount employeeAccount;

    /** ユーザーに付与する権限（ロールなど）を保存するリストです */
    private final Collection<GrantedAuthority> authorites;

    /**
     * コンストラクタです。
     * 
     * データベースから取得した従業員データと、その人に与える権限を
     * このクラスの中にセットします。
     * 
     * @param employeeAccount データベースから取得した従業員データ
     * @param authorites      ユーザーに設定する権限のリスト
     */
    public EmployeeAccountDetails(EmployeeAccount employeeAccount, Collection<GrantedAuthority> authorites) {
        this.employeeAccount = employeeAccount;
        this.authorites = authorites;
    }

    /**
     * ユーザーに設定されている権限を返します。
     * 
     * ログインしたユーザーがどのページにアクセスできるかを
     * Spring Securityがチェックする時に使われます。
     * 
     * @return 権限データのコレクション
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorites;
    }

    /**
     * データベースから取得した、ハッシュ化済みのパスワードを返します。
     * 
     * ここで返したハッシュ化パスワードと、ユーザーがログイン画面で入力した
     * 生のパスワードを、Spring Securityが裏側で自動的に照合してくれます。
     * 
     * @return データベースに保存されている暗号化されたパスワード文字列
     */
    @Override
    public String getPassword() {
        return employeeAccount.getPassword();
    }

    /**
     * ログイン時にユーザー名として使用する文字列を返します。
     * 
     * 今回は employeeAccount.getName()（従業員の名前）を返すようにしています。
     * もしログイン画面で「社員ID（数字）」を入力させる設計に変更する場合は、
     * ここを社員IDを返すメソッドに書き換える必要があります。
     * 
     * @return ログイン画面のユーザー名に入力される文字列
     */
    @Override
    public String getUsername() {
        return employeeAccount.getName();
    }

    /**
     * アカウントの有効期限が切れていないかをチェックするメソッドです。
     * 
     * 今回の開発では有効期限の機能は使わないため、
     * 常に「期限内である」という意味の true を返しています。
     * 
     * @return 常に true （有効期限切れではない）
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * アカウントがロックされていないかをチェックするメソッドです。
     * 
     * データベースから取得した従業員データ（employeeAccount）のロック日時を確認し、
     * ロック期間（10分間）が経過しているかどうかを判定して結果を返します。
     * 
     * @return ロックされていなければ true、ロック中であれば false
     */
    @Override
    public boolean isAccountNonLocked() {
        // ロック日時（lockTime）がセットされていなければ、ロックされていないので true
        if (this.employeeAccount.getLockTime() == null) {
            return true;
        }

        // ロック解除時刻（ロックされた時間 ＋ 10分）を計算
        LocalDateTime unlockTime = this.employeeAccount.getLockTime().plusMinutes(10);

        // 現在時刻がロック解除時刻を過ぎている（after）なら、ロックは終了しているので true
        // まだ過ぎていない（before）なら、ロック中なので false を返す
        return LocalDateTime.now().isAfter(unlockTime);
    }

    /**
     * パスワード自体の有効期限が切れていないかをチェックするメソッドです。
     * 
     * 常に「期限内である」という意味の true を返しています。
     * 
     * @return 常に true （パスワードは有効）
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 該当のアカウントがシステム上で有効かどうかをチェックするメソッドです。
     * 
     * @return 常に true （アカウントは有効）
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
