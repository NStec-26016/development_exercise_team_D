package com.example.fullness.stationary.service;



import com.example.shop.entity.Employee;
import com.example.shop.form.AccountRegisterForm;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class AccountRegisterService {

    /**
     * 【モック】未登録の社員リストをダミーで返す
     */
    public List<Employee> getUnregisteredEmployees() {
        List<Employee> list = new ArrayList<>();
        
        Employee emp1 = new Employee();
        emp1.setId("1");
        emp1.setName("テスト 太郎");
        
        Employee emp2 = new Employee();
        emp2.setId("2");
        emp2.setName("サンプル 花子");
        
        list.add(emp1);
        list.add(emp2);
        return list;
    }

    /**
     * 【モック】IDから社員名をダミーで返す
     */
    public String getEmployeeNameById(String id) {
        if ("1".equals(id)) return "テスト 太郎";
        if ("2".equals(id)) return "サンプル 花子";
        return "未知の社員";
    }

    /**
     * 【モック】アカウント名の重複チェック（常に重複なしとする）
     */
    public boolean isAccountNameDuplicate(String accountName) {
        return false; 
    }

    /**
     * 【モック】登録処理（コンソールにログを出すだけ）
     */
    public void register(AccountRegisterForm form) {
        System.out.println("【Mock】アカウントを登録しました。ID: " + form.getEmployeeId() + ", アカウント名: " + form.getAccountName());
    }
}

